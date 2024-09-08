package com.example.demo.basic.shape;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.base.egl.BaseGLThread;
import com.example.demo.base.egl.GLCallback;
import com.example.demo.base.egl.GLSurface;
import com.example.demo.base.egl.SimpleTrianglesEGLHelper;
import com.example.opengles.databinding.ActivityShapeDrawBinding;

public class ShapeDrawActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private ActivityShapeDrawBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShapeDrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListener();

    }

    GLSurface minSurface;

    private void initListener() {
        glThread.start();
        binding.surfaceView.getHolder().addCallback(this);

        binding.surfaceView2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                minSurface = new GLSurface(holder.getSurface(), binding.surfaceView2.getMeasuredWidth(), binding.surfaceView2.getMeasuredHeight());
                glThread.addSurface(minSurface);
                glThread.requestRender();

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                glThread.removeSurface(minSurface);
            }
        });
        binding.surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glThread.requestRender();
            }
        });

    }

    private BaseGLThread glThread = new BaseGLThread(new GLCallback() {
        @Override
        public void onCrated() {

        }

        @Override
        public void onDestroy() {

        }
    }, new SimpleTrianglesEGLHelper());
    GLSurface surface;

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surface = new GLSurface(holder.getSurface(), binding.surfaceView.getMeasuredWidth(), binding.surfaceView.getMeasuredHeight());
        glThread.addSurface(surface);
        glThread.requestRender();
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        glThread.removeSurface(surface);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        glThread.requestExit();
    }
}