package com.example.demo.opengl

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import androidx.appcompat.app.AppCompatActivity
import com.example.jni.NativeOpengl
import com.example.opengles.databinding.ActivityOpenGlactivityBinding
import kotlin.random.Random

class OpenGLActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "OpenGLActivity"
    }

    private val binding by lazy {
        ActivityOpenGlactivityBinding.inflate(layoutInflater)
    }
    private val nativeOpengl by lazy {
        NativeOpengl()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.surfaceView.holder.addCallback(object : Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.d(
                    TAG,
                    "surfaceView surfaceCreated ${Thread.currentThread().name} ${holder.surface.hashCode()}"
                )
                nativeOpengl.surfaceCreate(holder.surface)

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.d(
                    TAG,
                    "surfaceView surfaceChanged ${Thread.currentThread().name} ${holder.surface.hashCode()}"
                )

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.d(
                    TAG,
                    "surfaceView surfaceDestroyed ${Thread.currentThread().name} ${holder.surface.hashCode()}"
                )

            }
        })
        binding.surfaceView2.holder.addCallback(object : Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })

        binding.surfaceView2.setOnClickListener {
            binding.surfaceView2.setOnClickListener {
                nativeOpengl.changeSurface(binding.surfaceView2.holder.surface)
                nativeOpengl.onSizeChanged(
                    binding.surfaceView2.width,
                    binding.surfaceView2.height
                )

                nativeOpengl.drawColor(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())

            }
        }
        binding.surfaceView.setOnClickListener {
            nativeOpengl.changeSurface(binding.surfaceView.holder.surface)
            nativeOpengl.onSizeChanged(
                binding.surfaceView.width,
                binding.surfaceView.height
            )
            nativeOpengl.drawColor(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
        }
    }
}