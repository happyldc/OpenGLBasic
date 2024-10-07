package com.example.demo.basic.fbo4

import android.os.Bundle
import android.view.SurfaceHolder
import com.example.demo.base.CameraBaseActivity
import com.example.demo.base.CameraManger
import com.example.demo.base.egl.BaseGLThread
import com.example.demo.base.egl.GLCallback
import com.example.demo.basic.SimpleSurfaceViewCallback
import com.example.opengles.databinding.ActivityFbocameraPreviewBinding

class FBOCameraPreviewActivity : CameraBaseActivity() {
    private val binding by lazy {
        ActivityFbocameraPreviewBinding.inflate(layoutInflater)
    }
    private val eglHelper by lazy {
        FBOCameraEGLHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListener()

    }

    private val cameraManager by lazy {
        CameraManger()
    }

    override fun onPermissionGrand() {
        cameraManager.init(this)

    }

    private val glThread: BaseGLThread by lazy {
        BaseGLThread(object : GLCallback {
            override fun onCrated() {
                cameraManager.setPreviewDisplay(eglHelper.surfaceTexture)
                cameraManager.startPreview()
                eglHelper.surfaceTexture.setOnFrameAvailableListener {
                    glThread.requestRender()
                }
            }

            override fun onDestroy() {
                cameraManager.stopPreview()
            }

        }, eglHelper)
    }

    private fun initListener() {
        glThread.requestRender()
        glThread.start()
//        binding.surfaceView.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                super.surfaceCreated(holder)
//                glThread.requestRender()
//                glThread.start()
//            }
//        })
//        binding.svBottomLeft.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                super.surfaceCreated(holder)
//                glThread.requestRender()
//            }
//        })
//        binding.svBottomRight.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                super.surfaceCreated(holder)
//                glThread.requestRender()
//            }
//        })
//        binding.svTopRight.holder.addCallback(object : SimpleSurfaceViewCallback(glThread) {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                super.surfaceCreated(holder)
//                glThread.requestRender()
//            }
//        })


    }

}