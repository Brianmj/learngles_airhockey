package com.brianj.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class AirHockeyRenderer(ctx: Context) : GLSurfaceView.Renderer
{
    val TAG = "AirHockeyRenderer"
    val colorBuffer = FloatArray(4)
    val depthBuffer = FloatArray(1)
    val context = ctx
    var viewportWidth: Int = 0
    var viewportHeight: Int = 0

    companion object {
        val POSITION_COMPONENT_COUNT = 2
        val BYTES_PER_FLOAT = 4
    }

    lateinit var vertexData: FloatBuffer

    init {
        val tableVertices = floatArrayOf(0f, 0f,        // triangle 1
                9f, 14f,
                0f, 14f,

                0f, 0f,         // triangle 2
                9f, 0f,
                9f, 14f,

                0f, 7f,         // line 1
                9f, 7f,

                4.5f, 2f,         // mallets
                4.5f, 12f)

        vertexData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

        vertexData.put(tableVertices).flip()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClearBufferfv(GLES32.GL_DEPTH, 0, depthBuffer, 0)
        GLES32.glClearBufferfv(GLES32.GL_COLOR, 0, colorBuffer, 0)


    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated")
        colorBuffer[0] = 0.607843f; colorBuffer[1] = 0.803921f; colorBuffer[2] = 0.992156f; colorBuffer[3] = 1.0f
        depthBuffer[0] = 1.0f

        val shaderManager = ShaderManager(context)
        //manager.buildGraphicsProgramAssets("myglsl.vert", "fragment.frag")
        //manager.buildGraphicsProgramRaw(R.raw.vertex, R.raw.fragment)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged, width: $width, height: $height")
        viewportHeight = height
        viewportWidth = width
        GLES20.glViewport(0, 0, width, height)
    }
}