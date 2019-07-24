package com.kolbapps.opengltests

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.kolbapps.opengltests.util.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class AirHockeyRenderer(private val context: Context): GLSurfaceView.Renderer {
    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val BYTES_PER_FLOAT = 4
        private const val U_COLOR = "u_Color"
        private const val A_POSITION = "a_Position"
    }

    private var vertexData: FloatBuffer
    private var program = 0
    private var uColorLocation = 0
    private var aPositionLocation = 0

    init {
        // For culling, vertices must be defined in a Counter Clockwise way
        // to be shown from front to back
//        val tableVertices = floatArrayOf(
//            0f, 0f,
//            0f, 14f,
//            9f, 14f,
//            9f, 0f
//        )

        val tableVerticesWithTriangles = floatArrayOf(
            // Triangle 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,

            // Triangle 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,

            // Line 1
            -0.5f, 0f,
            0.5f, 0f,

            // Mallets
            0f, -0.25f,
            0f, 0.25f
        )

        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        vertexData.put(tableVerticesWithTriangles)
    }

    // Called once after surface is created
    override fun onSurfaceCreated(ignored: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        val vertexShaderSource = readTextFileFromResource(
            context, R.raw.simple_vertex_shader)
        val fragmentShaderSource = readTextFileFromResource(
            context, R.raw.simple_fragment_shader)

        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)
        program = linkProgram(vertexShader, fragmentShader)
        glUseProgram(program)

        if (LoggerConfig.ON) {
            validateProgram(program)
        }

        uColorLocation = glGetUniformLocation(program, U_COLOR)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
            false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }

    // Portrait to landscape and vice versa
    override fun onSurfaceChanged(ignored: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(ignored: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        glDrawArrays(GL_TRIANGLES, 0, 6)

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        glDrawArrays(GL_LINES, 6, 2)

        // Draw the first mallet blue.
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        glDrawArrays(GL_POINTS, 8, 1)

        // Draw the second mallet red.
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        glDrawArrays(GL_POINTS, 9, 1)
    }
}