package com.example.kyle.kotlintest

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.ScaleGestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
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

    val mScaleDetector by lazy{
        ScaleGestureDetector(applicationContext, ScaleListener())
    }

    var mScaleFactor = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagelayout)
        val bm = AsyncDownload().execute().get()
        img.setImageBitmap(bm)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(event)
        return true
        //return super.onTouchEvent(event)
    }

    inner class AsyncDownload : AsyncTask<String, String, Bitmap>(){
        override fun doInBackground(vararg p0: String?): Bitmap {
            val im_stream = URL("http://services.swpc.noaa.gov/images/aurora-forecast-northern-hemisphere.jpg").content
            return BitmapFactory.decodeStream(im_stream as InputStream)
        }
    }


    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        lateinit var initPoint: PointF
        lateinit var currPoint: PointF

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            initPoint = PointF(detector.focusX, detector.focusY)
            return super.onScaleBegin(detector)
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            initPoint = PointF()
            currPoint = PointF(detector.focusX, detector.focusY)
            /*mScaleFactor *= detector.getScaleFactor()
            mScaleFactor = maxOf(0.1f, minOf(mScaleFactor, 10f))
            Log.v("IMAGE", "scale factor: " + mScaleFactor)*/
            val newMatrix = Matrix()

            val mp = PointF((detector.currentSpanX+detector.previousSpanX)/2, (detector.currentSpanY+detector.previousSpanY)/2)
            val middleImage = PointF((initPoint.x+currPoint.x)/2, (initPoint.y+currPoint.y)/2)
            newMatrix.postScale(currPoint.length()/initPoint.length(), currPoint.length()/initPoint.length(), middleImage.x, middleImage.y)
            img.imageMatrix = newMatrix

            return super.onScale(detector)

        }
    }
}