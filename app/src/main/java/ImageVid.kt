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
import com.example.imgtovid.DeleteImages
import com.example.kyle.kotlintest.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.URL
import com.example.imgtovid.VideoDrawable

/**
 * Created by Kyle on 2/2/2018.
 */
class ImageVid: AppCompatActivity(){
    var play: Boolean = false

    val img by lazy{
        findViewById<ImageView>(R.id.inetImage)
    }

    val tmp by lazy{
        VideoDrawable(resources)
    }

    val test by lazy{
        JsonDownload().execute().get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagelayout)
        //http://services.swpc.noaa.gov/products/animations/ovation-north.json
        val base_url = "http://services.swpc.noaa.gov"
        test.forEach { url ->  AsyncDownload("$filesDir/24hr").execute("$base_url$url")}
        AsyncCheck().execute()
        val testArray1: Array<String> = test.toTypedArray()
        DeleteOvN("$filesDir/24hr").execute(testArray1)
        findViewById<ImageView>(R.id.inetImage).setOnClickListener {
            if (tmp.isRunning) tmp.stop() else tmp.start()
            play = !play
        }
    }

    override fun onResume() {
        super.onResume()
        if (play) tmp.start()
    }

    override fun onPause() {
        super.onPause()
        if (tmp.isRunning) tmp.stop()
    }



    //For ovation-north only
    inner class DeleteOvN(path: String) : DeleteImages(path){
        override fun getDateTime(file: String): Pair<Int, Int> {
            val fiSplit = file.split("_",".")
            val fiHr = fiSplit[fiSplit.lastIndex - 1].toInt() / 100
            val fiMn = fiSplit[fiSplit.lastIndex - 1].toInt() % 100

            return Pair(fiSplit[fiSplit.lastIndex - 2].toInt(), fiHr*60+fiMn)
        }
    }

    inner class AsyncDownload(private val path: String) : AsyncTask<String, Void, ByteArray>() {


        override fun doInBackground(vararg p0: String): ByteArray{
            val f_name = p0[0].split('/').last()
            if (!File(path,f_name).exists()) {
                val im_stream = URL(p0[0]).content
                val im_bytes: ByteArray = (im_stream as InputStream).readBytes()
                im_stream.close()
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
            @Suppress("UNCHECKED_CAST")
            val json = parser.parse(URL(url).content as InputStream) as JsonArray<JsonObject>

            json.forEach{j -> urls.add(j.get("url") as String)}
            return urls

        }
    }

}