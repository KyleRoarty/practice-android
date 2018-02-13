package com.example.kyle.kotlintest

import android.content.Context
import android.graphics.*
import com.beust.klaxon.Parser
import android.graphics.drawable.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.example.kyle.kotlintest.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * Created by Kyle on 2/2/2018.
 */
class ImageVid: AppCompatActivity(){
    val img by lazy{
        findViewById<ImageView>(R.id.inetImage)
    }

    val tmp by lazy{
        VideoDrawable()
    }

    val test by lazy{
        JsonDownload().execute().get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagelayout)
        //http://services.swpc.noaa.gov/products/animations/ovation-north.json
        val base_url = "http://services.swpc.noaa.gov"
        test.forEach { url ->  AsyncDownload().execute("$base_url$url")}
        AsyncCheck().execute()
        val testArray1: Array<String> = test.toTypedArray()
        DeleteFiles().execute(testArray1)
        findViewById<ImageView>(R.id.inetImage).setOnClickListener {
            if (tmp.isRunning) tmp.stop() else tmp.start()
        }
    }


    inner class VideoDrawable : Drawable(), Drawable.Callback, Animatable, Runnable {
        var mCurrDrawable: Drawable? = null
        var mBmpArray: ArrayList<ByteArray> = ArrayList<ByteArray>()
        var mCurrIdx: Int = -1
        var mRunning : Boolean = false

        fun addImage(bmp: ByteArray){
            mBmpArray.add(bmp)

            if (mCurrDrawable == null){
                mCurrDrawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(bmp, 0, bmp.size))
            }
        }

        fun size(): Int{
            return mBmpArray.size
        }

        fun setDrawable(draw: Drawable){
            mCurrDrawable = draw
        }

        fun setFrame() {
            if (mCurrIdx+1 >= mBmpArray.size)
                mCurrIdx = 0
            else
                mCurrIdx = mCurrIdx+1

            val d = BitmapDrawable(resources, BitmapFactory.decodeByteArray(mBmpArray[mCurrIdx], 0, mBmpArray[mCurrIdx].size))
            mCurrDrawable = d
            d.mutate()
            d.setVisible(isVisible, true)
            d.state = state
            d.level = level
            d.bounds = bounds

            scheduleSelf(this, SystemClock.uptimeMillis()+(1000/10))

            invalidateSelf()
        }

        override fun run() {
            setFrame()
        }

        override fun start() {
            if (!isRunning) {
                mRunning = true
                run()
            }
        }

        override fun stop() {
            if (isRunning) {
                mRunning = false
                unscheduleSelf(this)
            }
        }

        override fun isRunning(): Boolean {
            return mRunning
        }

        override fun draw(p0: Canvas?) {
            mCurrDrawable?.draw(p0)
        }

        override fun getPadding(padding: Rect?): Boolean {
            return mCurrDrawable?.getPadding(padding) ?: super.getPadding(padding)
        }

        override fun setAlpha(alpha: Int) {
            mCurrDrawable?.mutate()?.alpha = alpha
        }

        override fun getAlpha(): Int {
            return mCurrDrawable?.alpha ?: 0xFF
        }

        override fun setColorFilter(cf: ColorFilter?) {
            mCurrDrawable?.mutate()?.colorFilter = cf
        }

        override fun onBoundsChange(bounds: Rect?) {
            mCurrDrawable?.bounds = bounds
        }

        override fun getOpacity(): Int {
            //Dunno what this does
            return PixelFormat.TRANSPARENT
        }

        override fun getIntrinsicHeight(): Int {
            return mCurrDrawable?.intrinsicHeight ?: -1
        }

        override fun getIntrinsicWidth(): Int {
            return mCurrDrawable?.intrinsicWidth ?: -1
        }

        override fun invalidateDrawable(p0: Drawable?) {
            if(p0 == mCurrDrawable && callback != null) {
                callback.invalidateDrawable(this)
            }
        }

        override fun scheduleDrawable(p0: Drawable?, p1: Runnable?, p2: Long) {
            if(p0 == mCurrDrawable && callback != null){
                callback.scheduleDrawable(this, p1, p2)
            }
        }

        override fun unscheduleDrawable(p0: Drawable?, p1: Runnable?) {
            if(p0 == mCurrDrawable && callback != null){
                callback.unscheduleDrawable(this, p1)
            }
        }

    }

    //For ovation-north only
    inner class DeleteFiles : AsyncTask<Array<String>, Void, Int>(){
        //Returns date as int, Time in mins
        private fun getDateTime(file: String): Pair<Int, Int> {
            val fiSplit = file.split("_",".")
            val fiHr = fiSplit[fiSplit.lastIndex - 1].toInt() / 100
            val fiMn = fiSplit[fiSplit.lastIndex - 1].toInt() % 100

            return Pair(fiSplit[fiSplit.lastIndex - 2].toInt(), fiHr*60+fiMn)
        }

        override fun doInBackground(vararg p0: Array<String>): Int {
            val dlFL: Array<String> = p0.get(0)
            val dlFileList : Array<String> = dlFL.sortedArray()
            val dlFirst = getDateTime(dlFileList.first())

            val path: String = "$filesDir/24hr"
            val svFL: Array<String> = File(path).list()
            val svFileList: Array<String> = svFL.sortedArray()

            var loopIdx = 0
            while (true) {
                val cmpFile = getDateTime(svFileList[loopIdx])
                if (24*60*(dlFirst.first - cmpFile.first) + dlFirst.second - cmpFile.second <= 0){
                    break
                }
                File(path,svFileList[loopIdx]).delete()
                loopIdx += 1
            }

            return 1
        }
    }

    inner class AsyncDownload : AsyncTask<String, Void, ByteArray>() {
        private val path: String = "$filesDir/24hr"

        override fun doInBackground(vararg p0: String): ByteArray{
            val f_name = p0[0].split('/').last()
            if (!File(path,f_name).exists()) {
                val im_stream = URL(p0[0]).content
                val im_bytes: ByteArray = (im_stream as InputStream).readBytes()
                (im_stream as InputStream).close()
                saveBytes(im_bytes, f_name)
                return im_bytes
            } else {
                return File(path,f_name).readBytes()
            }
        }

        override fun onPostExecute(result: ByteArray) {
            super.onPostExecute(result)
            tmp.addImage(result)
        }

        private fun saveBytes (imgBytes: ByteArray, imgName: String) {
            val f = File(path,imgName)
            //Log.d("asdf", "${f.absolutePath},\n${f.name},\n${f.path},\n${f.parentFile},\n${f.exists()}")
            if (f.exists()) return
            f.parentFile.mkdir()
            f.writeBytes(imgBytes)
        }
    }

    inner class AsyncCheck: AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg p0: Void?): Boolean{
            while (test.size != tmp.size()){}
            return test.size == tmp.size()
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result){
                findViewById<ProgressBar>(R.id.loadingSpinny).visibility = View.GONE
                img.setImageDrawable(tmp)
                //tmp.start()
            }
        }
    }

    inner class JsonDownload: AsyncTask<String, String, ArrayList<String>>() {
        override fun doInBackground(vararg p0: String?): ArrayList<String> {
            val urls: ArrayList<String> = arrayListOf<String>()
            val parser: Parser = Parser()
            val url: String = "http://services.swpc.noaa.gov/products/animations/ovation-north.json"
            //val url: String = "http://services.swpc.noaa.gov/products/animations/GOES-13-CS-PTHK-0.4.json"
            val json = parser.parse(URL(url).content as InputStream) as JsonArray<JsonObject>

            json.forEach{j -> urls.add(j.get("url") as String)}
            return urls

        }
    }

}