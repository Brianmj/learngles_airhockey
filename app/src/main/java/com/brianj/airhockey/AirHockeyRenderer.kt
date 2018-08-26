package com.brianj.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES31
import android.opengl.GLES32
import android.opengl.GLES32.*
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
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
    lateinit var shaderManager: ShaderManager
    lateinit var airHockeyProgramId: UUID

    companion object {
        val POSITION_COMPONENT_COUNT = 2
        val BYTES_PER_FLOAT = 4
    }

    lateinit var vertexData: FloatBuffer

    init {
        val tableVertices = floatArrayOf(
                -0.5f, -0.5f,        // triangle 1
                0.5f, 0.5f,
                -0.5f, 0.5f,

                -0.5f, -0.5f,         // triangle 2
                0.5f, -0.5f,
                0.5f, 0.5f,

                -0.5f, 0f,         // line 1
                0.5f, 0f,

                0f, -0.25f,         // mallets
                0f, 0.25f,

                0f, 0f)             // puck

        vertexData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

        vertexData.put(tableVertices).flip()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClearBufferfv(GL_DEPTH, 0, depthBuffer, 0)
        glClearBufferfv(GL_COLOR, 0, colorBuffer, 0)

        val programObject = shaderManager.retrieveProgram(airHockeyProgramId)
        // set the fragment output to white
        GLES31.glProgramUniform4f(programObject,
                8, 1f, 1f, 1f, 1f)

        // draw the board
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        GLES31.glProgramUniform4f(programObject,
                8, 1f, 0f, 0f, 1f)
        // draw the red divider line
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        // draw the first mallet blue
        GLES31.glProgramUniform4f(programObject, 8, 0f, 0f, 1f, 1f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

        // draw the second mallet red
        GLES31.glProgramUniform4f(programObject, 8, 1f, 0f, 0f, 1f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)

        // draw the black puck
        glProgramUniform4f(programObject, 8, 0f, 0f, 0f, 1f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 10, 1)


    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated")
        colorBuffer[0] = 0.607843f; colorBuffer[1] = 0.803921f; colorBuffer[2] = 0.992156f; colorBuffer[3] = 1.0f
        depthBuffer[0] = 1.0f

        shaderManager = ShaderManager(context)
        airHockeyProgramId = shaderManager.buildGraphicsProgramRaw(R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader)
        glUseProgram(shaderManager.retrieveProgram(airHockeyProgramId))

        vertexData.position(0)
        GLES20.glVertexAttribPointer(0, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false,
                0, vertexData)
        GLES20.glEnableVertexAttribArray(0)


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