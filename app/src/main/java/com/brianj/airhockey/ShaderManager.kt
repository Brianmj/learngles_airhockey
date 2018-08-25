package com.brianj.airhockey

import android.content.Context
import android.opengl.GLES20
import java.util.*
import android.opengl.GLES32.*
import java.io.InputStream

/**
 * Created by brianj on 7/21/17.
 */
class ShaderManager(val context: Context)
{
    private var programMap: MutableMap<UUID, Int>

    init {
        programMap = mutableMapOf()
    }

    fun buildGraphicsProgramRaw(vertexFileId: Int, fragmentFileId: Int): UUID
    {
        val programId = doBuildGraphicsProgramRaw(vertexFileId, fragmentFileId)
        // we have a valid programId.
        val uuid = addProgramToManager(programId)

        return uuid
    }

    fun buildGraphicsProgramAssets(vertexFile: String, fragmentFile: String): UUID
    {
        val programId = doBuildGraphicsProgramAssets(vertexFile, fragmentFile)
        // we have a valid programId.
        val uuid = addProgramToManager(programId)

        return uuid
    }

    fun activateProgram(uuid: UUID)
    {
        val programId = retrieveProgram(uuid)
        GLES20.glUseProgram(programId)
    }

    fun disableProgram()
    {
        GLES20.glUseProgram(0)
    }

    private fun doBuildGraphicsProgramRaw(vertexFileId: Int, fragmentFileId: Int): Int
    {
        val vertexShader = buildVertex(vertexFileId)
        val fragmentShader = buildFragment(fragmentFileId)

        val programId = generateProgram()
        attachShaderToProgram(programId, vertexShader)
        attachShaderToProgram(programId, fragmentShader)

        linkProgram(programId)
        checkProgramStatus(programId, "Graphics program")

        detachAndDeleteShader(programId, vertexShader)
        detachAndDeleteShader(programId, fragmentShader)

        return programId
    }

    private fun doBuildGraphicsProgramAssets(vertexFile: String, fragmentFile: String): Int
    {
        val vertexShader = buildVertex(vertexFile)
        val fragmentShader = buildFragment(fragmentFile)

        val programId = generateProgram()
        attachShaderToProgram(programId, vertexShader)
        attachShaderToProgram(programId, fragmentShader)

        linkProgram(programId)
        checkProgramStatus(programId, "Graphics program")

        detachAndDeleteShader(programId, vertexShader)
        detachAndDeleteShader(programId, fragmentShader)

        return programId
    }

    private fun openStreamToFile(fileName: String): InputStream
    {
        val inputStream = context.assets.open(fileName)

        return inputStream
    }

    private fun openStreamToFile(resource: Int): InputStream
    {
        val inputStream = context.resources.openRawResource(resource)

        return inputStream
    }

    private fun readSource(inputStream: InputStream): String
    {
        /*val size = channel.size()
        val buffer = ByteBuffer.allocate(size.toInt())
        val bytesRead = channel.read(buffer)

        if(bytesRead != size.toInt())
            throw Exception("Not all bytes could be read. Bytes read: $bytesRead. size of file: $size")

        val source = String(buffer.array())

        channel.close()*/

        val bufferedReader = inputStream.bufferedReader()
        val source = bufferedReader.readText()
        return source
    }

    private fun buildVertex(vertexFile: String): Int
    {
        val vertexStream = openStreamToFile(vertexFile)
        val vertexSource = readSource(vertexStream)
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        checkShaderStatus(vertexShader, "Vertex")
        vertexStream.close()

        return vertexShader
    }

    private fun buildVertex(vertexFileId: Int): Int
    {
        val vertexStream = openStreamToFile(vertexFileId)
        val vertexSource = readSource(vertexStream)
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        checkShaderStatus(vertexShader, "Vertex")
        vertexStream.close()

        return vertexShader
    }

    private fun buildFragment(fragmentFile: String): Int
    {
        val fragmentStream = openStreamToFile(fragmentFile)
        val fragmentSource = readSource(fragmentStream)
        val fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource)
        checkShaderStatus(fragmentShader, "Fragment")
        fragmentStream.close()

        return fragmentShader
    }

    private fun buildFragment(fragmentFileId: Int): Int
    {
        val fragmentStream = openStreamToFile(fragmentFileId)
        val fragmentSource = readSource(fragmentStream)
        val fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource)
        checkShaderStatus(fragmentShader, "Fragment")
        fragmentStream.close()

        return fragmentShader
    }

    private fun compileShader(shaderType: Int, shaderSource: String): Int
    {
        val shader = GLES20.glCreateShader(shaderType)
        GLES20.glShaderSource(shader, shaderSource)
        GLES20.glCompileShader(shader)

        return shader
    }

    private fun linkProgram(programId: Int)
    {
        GLES20.glLinkProgram(programId)
    }

    private fun checkShaderStatus(shader: Int, shaderType: String)
    {
        var success = IntArray(1)
        GLES20.glGetShaderiv(shader, GL_COMPILE_STATUS, success, 0)

        if(success[0] <= GL_FALSE)
        {
            // failure
            val errorString = GLES20.glGetShaderInfoLog(shader)
            val resultString = "$shaderType: $errorString"
            throw RuntimeException(resultString)
        }
    }

    private fun checkProgramStatus(program: Int, programType: String)
    {
        var success = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, success, 0)

        if(success[0] <= GL_FALSE)
        {
            // failure
            val errorString = GLES20.glGetProgramInfoLog(program)
            val resultString = "$programType: $errorString"
            throw RuntimeException(resultString)
        }

        GLES20.glValidateProgram(program)
        val validationStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validationStatus, 0)

        if(validationStatus[0] <= GL_FALSE)
        {
            val errorString = GLES20.glGetProgramInfoLog(program)
            val resultString = "$programType: $errorString"
            throw RuntimeException(resultString)
        }
    }

    private fun generateProgram(): Int = GLES20.glCreateProgram()

    private fun attachShaderToProgram(program: Int, shader: Int) {
        GLES20.glAttachShader(program, shader)
    }

    private fun detachAndDeleteShader(programId: Int, shaderId: Int)
    {
        GLES20.glDetachShader(programId, shaderId)
        GLES20.glDeleteShader(shaderId)
    }

    private fun addProgramToManager(programId: Int): UUID
    {
        val uuid = UUID.randomUUID()
        programMap[uuid] = programId

        return uuid
    }

    private fun removeProgramFromManager(uuid: UUID): Int?
    {
        val programId = programMap[uuid]
        programId?.let {
            programMap.remove(uuid)
        }

        return programId
    }

    private fun retrieveProgram(uuid: UUID): Int
    {
        val programId = programMap.get(uuid)


        programId?.let {
            return it
        }

        if(programId == null)
            throw RuntimeException("Invalid uuid: $uuid")

        return programId
    }

}