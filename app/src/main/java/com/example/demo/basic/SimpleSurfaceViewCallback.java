package com.example.demo.basic;

import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.example.demo.base.egl.BaseGLThread;
import com.example.demo.base.egl.GLSurface;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-12
 */
public class SimpleSurfaceViewCallback implements SurfaceHolder.Callback {
    private GLSurface glSurface;
    private BaseGLThread glThread;

    public SimpleSurfaceViewCallback(BaseGLThread glThread) {
        this.glThread = glThread;
    }

    public GLSurface getGlSurface() {
        return glSurface;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        glSurface = new GLSurface(holder.getSurface(), holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        glThread.addSurface(glSurface);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        glSurface.setViewport(0, 0, width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        glThread.removeSurface(glSurface);

    }
}
