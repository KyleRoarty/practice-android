package com.example.kyle.kotlintest

import android.graphics.*
import com.beust.klaxon.Parser
import android.graphics.drawable.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.animation.Animation
import android.widget.ImageView
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.example.kyle.kotlintest.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.URL

/**
 * Created by Kyle on 2/2/2018.
 */
class ImageVid: AppCompatActivity(){
    val img by lazy{
        findViewById<ImageView>(R.id.inetImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagelayout)
        //http://services.swpc.noaa.gov/products/animations/ovation-north.json
        val base_url = "http://services.swpc.noaa.gov"

        val ani = AnimationDrawable()
        val tmp = VideoDrawable()
        val test = JsonDownload().execute().get()
        //test.forEach { url ->  ani.addFrame(BitmapDrawable(resources, AsyncDownload().execute("$base_url$url").get()), 500)}
        test.forEach { url ->  tmp.addImage(AsyncDownload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$base_url$url").get())}
        //ani.addFrame(BitmapDrawable(resources, AsyncDownload().execute("$base_url${test[0]}").get()), 500)
        //tmp.setDrawable(BitmapDrawable(resources, AsyncDownload().execute("$base_url${test[0]}").get()))

        //tmp.addImage(AsyncDownload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$base_url${test[0]}").get())

        img.setImageDrawable(tmp)
        //img.setImageDrawable(ani)
        //ani.start()
        tmp.start()

    }

    inner class VideoDrawable : Drawable(), Drawable.Callback, Animatable, Runnable {
        var mCurrDrawable: Drawable? = null
        var mBmpArray: ArrayList<ByteArray> = ArrayList<ByteArray>()
        var mCurrIdx: Int = -1

        fun addImage(bmp: ByteArray){
            mBmpArray.add(bmp)

            if (mCurrDrawable == null){
                mCurrDrawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(bmp, 0, bmp.size))
            }
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
            if (d != null) {
                d.mutate()
                d.setVisible(isVisible(), true)
                d.setState(getState())
                d.setLevel(getLevel())
                d.setBounds(getBounds())
            }
            scheduleSelf(this, SystemClock.uptimeMillis()+(1000/15))

            invalidateSelf()
        }

        override fun run() {
            setFrame()
        }

        override fun start() {
            if (!isRunning)
                run()
        }

        override fun stop() {
            if (isRunning)
                unscheduleSelf(this)
        }

        override fun isRunning(): Boolean {
            return mCurrIdx > -1
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

    inner class AsyncDownload : AsyncTask<String, String, ByteArray>() {
        override fun doInBackground(vararg p0: String?): ByteArray{
            val im_stream = URL(p0[0]).content
            val im_bytes: ByteArray = (im_stream as InputStream).readBytes()
            (im_stream as InputStream).close()
            return im_bytes
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