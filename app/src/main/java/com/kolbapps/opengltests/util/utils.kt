package com.kolbapps.opengltests.util

import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20.*
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.opengl.GLES20.GL_COMPILE_STATUS
import android.opengl.GLES20.GL_LINK_STATUS
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.GL_VALIDATE_STATUS
import android.opengl.GLES20.glValidateProgram


object LoggerConfig {
    val ON: Boolean
        get() = true
}

fun readTextFileFromResource(context: Context, resourceId: Int): String {
    val body = StringBuilder()

    try {
        val inputStream = context.resources.openRawResource(resourceId)
        val inputStreamReader = InputStreamReader(inputStream)

        val bufferedReader = BufferedReader(inputStreamReader)

        var nextLine: String?
        while (true) {
            nextLine = bufferedReader.readLine()
            if(nextLine == null) break

            body.append(nextLine)
            body.append('\n')
        }
    } catch (e: IOException) {
        throw RuntimeException("Could not open resource: $resourceId", e)
    } catch (nfe: Resources.NotFoundException) {
        throw RuntimeException("Resource not found: $resourceId", nfe)
    }

    return body.toString()
}

fun compileVertexShader(shaderCode: String): Int {
    return compileShader(GL_VERTEX_SHADER, shaderCode)
}

fun compileFragmentShader(shaderCode: String): Int {
    return compileShader(GL_FRAGMENT_SHADER, shaderCode)
}

private fun compileShader(type: Int, shaderCode: String): Int {
    val shaderObjectId = glCreateShader(type)
    if (shaderObjectId == 0) {
        if (LoggerConfig.ON) {
            Log.w("open_gl", "Could not create new shader.")
        }
        return 0
    }

    glShaderSource(shaderObjectId, shaderCode)
    glCompileShader(shaderObjectId)

    val compileStatus = IntArray(1)
    glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

    if(compileStatus[0] == 0) {
        // If it failed, delete the shader object.
        glDeleteShader(shaderObjectId)
        if (LoggerConfig.ON) {
            Log.w("open_gl", "Compilation of shader failed.")
        }
        return 0
    }

    if(LoggerConfig.ON) {
        // Print the shader info log to the Android log output.
        Log.v("open_gl",
            "Results of compiling source:\n$shaderCode\n${glGetShaderInfoLog(shaderObjectId)}")
    }

    return shaderObjectId
}

fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
    val programObjectId = glCreateProgram()
    if (programObjectId == 0) {
        if (LoggerConfig.ON) {
            Log.w("open_gl", "Could not create new program")
        }

        return 0
    }

    glAttachShader(programObjectId, vertexShaderId)
    glAttachShader(programObjectId, fragmentShaderId)
    glLinkProgram(programObjectId)

    val linkStatus = IntArray(1)
    glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)

    if (LoggerConfig.ON) {
        // Print the program info log to the Android log output.
        Log.v("open_gl",
            "Results of linking program:\n${glGetProgramInfoLog(programObjectId)}")
    }

    if (linkStatus[0] == 0) {
        // If it failed, delete the program object.
        glDeleteProgram(programObjectId)
        if (LoggerConfig.ON) {
            Log.w("open_gl", "Linking of program failed.")
        }
        return 0
    }

    return programObjectId
}

fun validateProgram(programObjectId: Int): Boolean {
    glValidateProgram(programObjectId)
    val validateStatus = IntArray(1)
    glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
    Log.v(
        "open_gl",
        "Results of validating program: ${validateStatus[0]}\nLog:${glGetProgramInfoLog(programObjectId)}"
    )
    return validateStatus[0] != 0
}