package com.example.demo.basic.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.system.Os.close
import com.example.demo.base.CameraBaseActivity
import com.example.demo.base.CameraManger
import com.example.demo.basic.camera.CameraRenderer.OnSurfaceListener
import com.example.opengles.databinding.ActivityCameraGlsurfaceViewBinding
import java.io.IOException


class CameraGLSurfaceViewActivity : CameraBaseActivity() {
    private val binding by lazy {
        ActivityCameraGlsurfaceViewBinding.inflate(layoutInflater)
    }
    private val cameraManger by lazy { CameraManger() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onPermissionGrand() {
        binding.surfaceView.setEGLContextClientVersion(2)
        val renderer = CameraRenderer()
        binding.surfaceView.setRenderer(renderer)
//        cameraManger.init(this)
        //脏模式，数据需要刷新的时候进行绘制
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        renderer.setOnSurfaceListener(object : OnSurfaceListener {
            override fun onSurfaceCreate(surfaceTexture: SurfaceTexture) {
//                cameraManger.setPreviewDisplay(surfaceTexture)
//                cameraManger.startPreview()
                openCamera(surfaceTexture)
            }

            override fun onFrameAvailable() {
                binding.surfaceView.requestRender()
            }
        })

    }
    private var mCamera:Camera?=null
    private fun openCamera(surfaceTexture: SurfaceTexture) {
        mCamera = Camera.open(0)
        try {

            mCamera?.setPreviewTexture(surfaceTexture)
            val parameters: Camera.Parameters = mCamera?.getParameters()!!
            if (parameters != null) {
                //对焦，"auto"只对焦一次，某些机型可能不支持这种持续对焦的方式
                //http://blog.csdn.net/huweigoodboy/article/details/51378751
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
                parameters.setPreviewSize(1280, 720)
                mCamera?.setParameters(parameters)
            }
            //这个模式下，不生效，可以通过设置纹理坐标进行变换
//            mCamera.setDisplayOrientation(90);
            mCamera?.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}