package com.example.imgtovid

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock

/**
 * Created by Kyle on 3/8/2018.
 */
class VideoDrawable(val res: Resources) : Drawable(), Drawable.Callback, Animatable, Runnable {
    var mCurrDrawable: Drawable? = null
    var mBmpArray: ArrayList<ByteArray> = ArrayList<ByteArray>()
    var mCurrIdx: Int = -1
    var mRunning : Boolean = false
    var frameRate: Int = 10

    fun addImage(bmp: ByteArray){
        mBmpArray.add(bmp)

        if (mCurrDrawable == null){
            mCurrDrawable = BitmapDrawable(res, BitmapFactory.decodeByteArray(bmp, 0, bmp.size))
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

        val d = BitmapDrawable(res, BitmapFactory.decodeByteArray(mBmpArray[mCurrIdx], 0, mBmpArray[mCurrIdx].size))
        mCurrDrawable = d
        d.mutate()
        d.setVisible(isVisible, true)
        d.state = state
        d.level = level
        d.bounds = bounds

        scheduleSelf(this, SystemClock.uptimeMillis()+(1000/frameRate))

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