package com.example.demo.basic.texture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.base.egl.BaseGLThread
import com.example.demo.base.egl.GLCallback
import com.example.demo.base.egl.GLSurface
import com.example.demo.base.egl.SimpleTextureEGLHelper
import com.example.opengles.R
import com.example.opengles.databinding.ActivityTextureBinding

class TextureActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTextureBinding.inflate(layoutInflater)
    }
    private val textureHelper by lazy {
        SimpleTextureEGLHelper()
    }
    private val glThread by lazy {
        BaseGLThread(object : GLCallback {
            override fun onCrated() {
            }

            override fun onDestroy() {
            }

        }, textureHelper)
    }

    var bitmapIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListener()
    }

    private var glSurface: GLSurface? = null
    private var glSurface2: GLSurface? = null
    private fun initListener() {
        glThread.start()
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                glSurface = GLSurface(
                    holder.surface,
                    binding.surfaceView.measuredWidth,
                    binding.surfaceView.measuredHeight
                )
                glThread.addSurface(glSurface)
                glThread.requestRender()

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                glThread.removeSurface(glSurface)
            }
        })
        //263*263
        binding.surfaceView2.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                glSurface2 = GLSurface(
                    holder.surface,
                    binding.surfaceView2.measuredWidth,
                    binding.surfaceView2.measuredHeight
                )
                glThread.addSurface(glSurface2)

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                glThread.removeSurface(glSurface2)
            }
        })
        //1080*1584
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
            bitmap?.let { bitmap ->
                textureHelper.setTextureBitmap(bitmap)
                glThread.requestRender()
            }
            bitmapIndex++

        }


    }


}