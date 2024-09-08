package com.example.demo.base.egl;

import android.opengl.EGL14;
import android.opengl.EGL15;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-07
 */
public abstract class EGLHelper {
    private static final String TAG = "EGLHelper";
    protected EGLContext eglContext;
    protected EGLDisplay eglDisplay;
    protected EGLSurface eglSurface;
    protected EGLConfig eglConfig;

    private EGLContext shareContext = EGL14.EGL_NO_CONTEXT;

    protected List<GLSurface> outputSurfaces = new ArrayList<>();


    public void initEGL() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL14 display");
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("Unable to initialize EGL14");
        }
        int[] displayAttr = new int[]{
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 0,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE};
        int[] num_config = new int[1];

        if (!EGL14.eglChooseConfig(eglDisplay, displayAttr, 0, null, 0, 1, num_config, 0)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        EGLConfig[] eglConfigs = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(eglDisplay, displayAttr, 0, eglConfigs, 0, 1, num_config, 0)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        eglConfig = eglConfigs[0];

        int[] contextAttr = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, shareContext, contextAttr, 0);
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("Unable to create GLContext");
        }
        // 这里要先创建一个surface 之后再初始化着色器  没有这一步之后的着色器初始化都有问题
        // 创建 PBuffer Surface
        int[] pbufferAttributes = {
                EGL14.EGL_WIDTH, 1,   // 设置 PBuffer 的宽度
                EGL14.EGL_HEIGHT, 1, // 设置 PBuffer 的高度
                EGL14.EGL_NONE
        };
        eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, pbufferAttributes, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Unable to create PBuffer surface");
        }

        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);

    }

    public boolean addSurface(GLSurface surface) {
        boolean result = makeOutputSurface(surface);
        outputSurfaces.add(surface);
        return result;
    }

    public void removeSurface(GLSurface surface) {
        EGL14.eglDestroySurface(eglDisplay, surface.eglSurface);
        outputSurfaces.remove(surface);
    }

    protected boolean makeOutputSurface(GLSurface surface) {
        try {
            //创建surface缓存
            switch (surface.type) {
                case GLSurface.TYPE_WINDOW_SURFACE: {
                    int[] attributes = {EGL14.EGL_NONE};
                    surface.eglSurface = EGL14.eglCreateWindowSurface(eglDisplay,
                            eglConfig, surface.surface, attributes, 0
                    );
                    break;
                }
                case GLSurface.TYPE_PBUFFER_SURFACE: {
                    final int[] attributes = {
                            EGL14.EGL_WIDTH, surface.viewport.width,
                            EGL14.EGL_HEIGHT, surface.viewport.height,
                            EGL14.EGL_NONE};
                    // 创建失败时返回EGL14.EGL_NO_SURFACE
                    surface.eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay,
                            eglConfig, attributes, 0);
                    break;
                }
                case GLSurface.TYPE_PIXMAP_SURFACE: {
                    Log.w(TAG, "nonsupport pixmap surface");
                    return false;
                }
                default:
                    Log.w(TAG, "surface type error " + surface.type);
                    return false;

            }

        } catch (Exception e) {
            Log.w(TAG, "can't create eglSurface");
            e.printStackTrace();
            surface.eglSurface = EGL14.EGL_NO_SURFACE;
            return false;
        }
        return true;
    }
    int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    protected void ensureOutSurfacesCreated() {
        // 渲染(绘制)
        for (GLSurface output : outputSurfaces) {
            if (output.eglSurface == EGL14.EGL_NO_SURFACE) {
                if (!makeOutputSurface(output))
                    continue;
            }

        }

    }
    public abstract void initShaders();

    public abstract void render();

    public abstract void destroy();

}
