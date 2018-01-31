package com.example.kyle.kotlintest

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import java.io.InputStream
import java.net.URL

/**
 * Created by Kyle on 1/31/2018.
 */
class DownloadActivity : AppCompatActivity() {
    val img by lazy{
        findViewById<ImageView>(R.id.inetImage)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagelayout)
        img.setImageDrawable(AsyncDownload().execute().get())
    }

    inner class AsyncDownload : AsyncTask<String, String, Drawable>(){
        override fun doInBackground(vararg p0: String?): Drawable {
            val im_stream = URL("http://services.swpc.noaa.gov/images/aurora-forecast-northern-hemisphere.jpg").content
            val d = Drawable.createFromStream(im_stream as InputStream, "src name")
            return d
        }

        /*override fun onPostExecute(result: Drawable?) {
            img.setImageDrawable(result)
            super.onPostExecute(result)
        }*/
    }
}