package com.example.demo.basic.fbo3

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.base.egl.BaseGLThread
import com.example.demo.base.egl.GLCallback
import com.example.demo.basic.SimpleSurfaceViewCallback
import com.example.demo.basic.fbo1.FBO1EGLHelper
import com.example.demo.basic.fbo1.FBO2EGLHelper
import com.example.opengles.R
import com.example.opengles.databinding.ActivityFbo3Binding

class FBO3Activity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFbo3Binding.inflate(layoutInflater)
    }

    private var bitmapIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.surfaceView.setOnClickListener {
            val bitmap = if (bitmapIndex % 3 == 0) {
                BitmapFactory.decodeResource(resources, R.drawable.t1)//858*1250
            } else if (bitmapIndex % 3 == 1) {
                BitmapFactory.decodeResource(resources, R.drawable.t2)
            } else if (bitmapIndex % 3 == 2) {
                BitmapFactory.decodeResource(resources, R.drawable.t3)

            } else {
                null
            }
            bitmapIndex++
            eglHelper.setBitmap(bitmap)
            glThread.requestRender()
        }
        initListener()
    }

    private val eglHelper by lazy {
        FBOTextureEGLHelper().apply {
            setReadBitmapCallback { buffer, width, height ->
                val bitmap = BitmapFactory.decodeByteArray(buffer.array(), 0, buffer.limit())
                binding.ivImageView.setImageBitmap(bitmap)
            }
        }
    }
    private val glThread: BaseGLThread by lazy {
        BaseGLThread(object : GLCallback {
            override fun onCrated() {
            }

            override fun onDestroy() {
            }

        }, eglHelper)
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