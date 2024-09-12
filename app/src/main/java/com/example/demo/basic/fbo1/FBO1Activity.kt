package com.example.demo.basic.fbo1

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.base.egl.BaseGLThread
import com.example.demo.base.egl.GLCallback
import com.example.demo.base.egl.GLSurface
import com.example.demo.base.egl.SimpleTrianglesStrideEGLHelper
import com.example.demo.basic.SimpleSurfaceViewCallback
import com.example.opengles.databinding.ActivityFbo1Binding

/**
 * Framebuffer 和 Renderbuffer
 */
class FBO1Activity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFbo1Binding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.surfaceView.setOnClickListener {
            glThread.requestRender()
        }
        initListener()
    }

    private val glThread: BaseGLThread by lazy {
        BaseGLThread(object : GLCallback {
            override fun onCrated() {
            }

            override fun onDestroy() {
            }

        }, FBO1EGLHelper())
    }

    private fun initListener() {
        binding.surfaceView.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
            override fun surfaceCreated(holder: SurfaceHolder) {
                super.surfaceCreated(holder)
                glThread.requestRender()
                glThread.start()
            }
        })
        binding.svBottomLeft.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
            override fun surfaceCreated(holder: SurfaceHolder) {
                super.surfaceCreated(holder)
                glThread.requestRender()
            }
        })
        binding.svBottomRight.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
            override fun surfaceCreated(holder: SurfaceHolder) {
                super.surfaceCreated(holder)
                glThread.requestRender()
            }
        })
        binding.svTopRight.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
            override fun surfaceCreated(holder: SurfaceHolder) {
                super.surfaceCreated(holder)
                glThread.requestRender()
            }
        })


    }
}