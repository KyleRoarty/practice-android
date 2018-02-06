package com.example.kyle.kotlintest

import com.beust.klaxon.Parser
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.*
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
        val test = JsonDownload().execute().get()
        test.forEach { url ->  ani.addFrame(BitmapDrawable(resources, AsyncDownload().execute("$base_url$url").get()), 500)}

        img.setImageDrawable(ani)
        ani.start()
    }

    inner class VideoDrawable : DrawableContainer(), Runnable, Animatable {

        override fun start() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isRunning(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun stop() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun run() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    inner class AsyncDownload : AsyncTask<String, String, Bitmap>() {
        override fun doInBackground(vararg p0: String?): Bitmap {
            val im_stream = URL(p0[0]).content
            return BitmapFactory.decodeStream(im_stream as InputStream)
        }
    }

    inner class JsonDownload: AsyncTask<String, String, ArrayList<String>>() {
        override fun doInBackground(vararg p0: String?): ArrayList<String> {
            val urls: ArrayList<String> = arrayListOf<String>()
            val parser: Parser = Parser()
            val json = parser.parse(URL("http://services.swpc.noaa.gov/products/animations/GOES-13-CS-PTHK-0.4.json").content as InputStream) as JsonArray<JsonObject>

            json.forEach{j -> urls.add(j.get("url") as String)}
            return urls

        }
    }

}