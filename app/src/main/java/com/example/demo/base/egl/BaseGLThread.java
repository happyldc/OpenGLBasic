package com.example.demo.base.egl;

import android.opengl.EGL14;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * GL线程
 *
 * @author HB.LDC
 * @date 2024-09-07
 */
public class BaseGLThread extends Thread {
    private static final String TAG = "BaseGLThread";
    private ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(100);

    private boolean isExit = false;
    protected GLCallback glCallback;
    protected EGLHelper eglHelper;

    public BaseGLThread(GLCallback glCallback, EGLHelper eglHelper) {
        this.eglHelper = eglHelper;
        this.glCallback = glCallback;
    }

    @Override
    public void run() {
        super.run();
        initEGL();
        onCreated();
        while (!isExit) {
            try {
                Event event = eventQueue.take();
                switch (event.getType()) {
                    case Event.ADD_SURFACE: {
                        //创建elgSurface
                        GLSurface surface = (GLSurface) event.getParams();
                        eglHelper.addSurface(surface);
                        break;
                    }
                    case Event.REMOVE_SURFACE: {
                        GLSurface surface = (GLSurface) event.getParams();
                        eglHelper.removeSurface(surface);
                        Log.d(TAG, "remove:" + surface);
                        break;
                    }
                    case Event.EXIT: {
                        isExit = true;
                        continue;
                    }
                    case Event.RENDER: {
                        eglHelper.render();
                        break;
                    }
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        onDestroy();
        eventQueue.clear();
        Log.d(TAG, getName() + ": render release");

    }


    private void initEGL() {
        eglHelper.initEGL();
        eglHelper.initShaders();
    }

    void onCreated() {
        if (glCallback != null) {
            glCallback.onCrated();
        }

    }

    void onDestroy() {
        eglHelper.destroy();
        if (glCallback != null) {
            glCallback.onDestroy();
        }


    }

    private void addEvent(Event event) {
        eventQueue.offer(event);
    }

    public void addSurface(final GLSurface surface) {
        Event event = new Event(Event.ADD_SURFACE, surface);
        eventQueue.offer(event);
    }

    public void removeSurface(final GLSurface surface) {
        Event event = new Event(Event.REMOVE_SURFACE, surface);
        if (!eventQueue.offer(event))
            Log.e(TAG, "queue full");
    }

    public void requestRender() {
        Event event = new Event(Event.RENDER);
        eventQueue.offer(event);
    }

    public void requestExit() {
        Event event = new Event(Event.EXIT);
        eventQueue.offer(event);
        // 等待线程结束，如果不等待，在快速开关的时候可能会导致资源竞争(如竞争摄像头)
        // 但这样操作可能会引起界面卡顿，择优取舍
        while (isAlive()) {
            try {
                this.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}
