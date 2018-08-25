package com.brianj.airhockey

import android.content.Context


import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by brianj on 6/21/17.
 */
public class MySurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs)
{
    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setRenderer(AirHockeyRenderer(context))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
}