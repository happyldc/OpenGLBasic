package com.example.demo.basic.camera;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-10-07
 */
public class CameraRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    int[] tex = new int[1];
    SurfaceTexture surfaceTexture;
    CameraTexture triangle;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mOnSurfaceListener != null) {
            mOnSurfaceListener.onFrameAvailable();
        }
    }

    public interface OnSurfaceListener {
        void onSurfaceCreate(SurfaceTexture surfaceTexture);
        void onFrameAvailable();
    }

    private OnSurfaceListener mOnSurfaceListener;

    public void setOnSurfaceListener(OnSurfaceListener onSurfaceListener) {
        this.mOnSurfaceListener = onSurfaceListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glGenTextures(1, tex, 0);
        surfaceTexture = new SurfaceTexture(tex[0]);
        if (mOnSurfaceListener != null) {
            mOnSurfaceListener.onSurfaceCreate(surfaceTexture);
        }
        surfaceTexture.setOnFrameAvailableListener(this);
        triangle = new CameraTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        surfaceTexture.updateTexImage();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        triangle.draw(tex[0]);
    }
}