package com.example.kyle.kotlintest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
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
        val bm = AsyncDownload().execute().get()
        img.setImageBitmap(bm)
    }

    inner class AsyncDownload : AsyncTask<String, String, Bitmap>(){
        override fun doInBackground(vararg p0: String?): Bitmap {
            val im_stream = URL("http://services.swpc.noaa.gov/images/aurora-forecast-northern-hemisphere.jpg").content
            return BitmapFactory.decodeStream(im_stream as InputStream)
        }
    }
}