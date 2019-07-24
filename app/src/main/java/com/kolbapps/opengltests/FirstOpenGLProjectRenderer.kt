package com.kolbapps.opengltests

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstOpenGLProjectRenderer: GLSurfaceView.Renderer {
    // Called once after surface is created
    override fun onSurfaceCreated(ignored: GL10?, config: EGLConfig?) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }

    // Portrait to landscape and vice versa
    override fun onSurfaceChanged(ignored: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(ignored: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
    }
}