package com.example.kyle.kotlintest

import android.graphics.*
import com.beust.klaxon.Parser
import android.graphics.drawable.*
import android.os.AsyncTask
import android.os.Bundle
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
        val test = JsonDownload().execute().get()
        //test.forEach { url ->  ani.addFrame(BitmapDrawable(resources, AsyncDownload().execute("$base_url$url").get()), 500)}
        val tmp = VideoDrawable()
        ani.addFrame(BitmapDrawable(resources, AsyncDownload().execute("$base_url${test[0]}").get()), 500)
        //tmp.setDrawable(BitmapDrawable(resources, AsyncDownload().execute("$base_url${test[0]}").get()))

        img.setImageDrawable(tmp)
        //img.setImageDrawable(ani)
        //ani.start()

    }

    inner class VideoDrawable : Drawable(), Drawable.Callback {
        var mCurrDrawable: Drawable? = null
        var mAlpha: Int = 0xFF

        fun setDrawable(draw: Drawable){
            mCurrDrawable = draw
        }

        override fun draw(p0: Canvas?) {
            mCurrDrawable?.draw(p0)
        }

        override fun getPadding(padding: Rect?): Boolean {
            return mCurrDrawable?.getPadding(padding) ?: super.getPadding(padding)
        }

        override fun setAlpha(alpha: Int) {
            mAlpha = alpha
            mCurrDrawable?.mutate()?.alpha = mAlpha
        }

        override fun getAlpha(): Int {
            return mAlpha
        }

        override fun setColorFilter(color: Int, mode: PorterDuff.Mode?) {
            mCurrDrawable?.mutate()?.setColorFilter(color, mode)
        }

        override fun setTint(tintColor: Int) {
            mCurrDrawable?.mutate()?.setTint(tintColor)
        }

        override fun onBoundsChange(bounds: Rect?) {
            mCurrDrawable?.bounds = bounds
        }

        override fun setAutoMirrored(mirrored: Boolean) {
            mCurrDrawable?.mutate()?.setAutoMirrored(mirrored)
        }

        override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
            return mCurrDrawable?.setVisible(visible, restart) ?: super.setVisible(visible, restart)
        }


        override fun getIntrinsicHeight(): Int {
            return mCurrDrawable?.intrinsicHeight ?: -1
        }

        override fun getIntrinsicWidth(): Int {
            return mCurrDrawable?.intrinsicWidth ?: -1
        }

        override fun setColorFilter(cf: ColorFilter?) {
            mCurrDrawable?.mutate()?.colorFilter = cf
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
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