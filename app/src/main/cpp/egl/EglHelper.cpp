//
// Created by lindiancheng on 2024/8/27.
//

#include "EglHelper.h"

EglHelper::EglHelper() {
    eglDisplay = EGL_NO_DISPLAY;
    eglSurface = EGL_NO_SURFACE;
    eglContext = EGL_NO_CONTEXT;
    eglConfig = NULL;

}

EglHelper::~EglHelper() {

}

int EglHelper::initEgl(EGLNativeWindowType window) {
    //1.获取默认显示设备
    eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (eglDisplay == EGL_NO_DISPLAY) {
        return -1;
    }
    //2.
    EGLint *version = new EGLint[2];
    if (!eglInitialize(eglDisplay, &version[0], &version[1])) {
        return -1;
    }
    //3. 设置显示属性 获取显示的配置
    const EGLint attrbs[] = {
            //颜色属性
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            //深度属性
            EGL_DEPTH_SIZE, 8,
            //模版测试
            EGL_STENCIL_SIZE, 8,
            //api
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            //数组结尾 习惯
            EGL_NONE
    };
    EGLint numConfig;
    if (!eglChooseConfig(eglDisplay, attrbs, NULL, 1, &numConfig)) {
        return -1;
    }
    //4.
    if (!eglChooseConfig(eglDisplay, attrbs, &eglConfig, numConfig, &numConfig)) {
        return -1;
    }
    //5.
    int attribList[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    eglContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, attribList);
    if (eglContext == EGL_NO_CONTEXT) {
        return -1;
    }
    //6.
    eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, window, NULL);
    if (eglSurface == EGL_NO_SURFACE) {
        return -1;
    }
    //7.
    if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
        return -1;
    }

    return 0;
}
void EglHelper::attachSurface(EGLNativeWindowType window){
    if (eglDisplay != EGL_NO_DISPLAY) {
        eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
    if (eglDisplay != EGL_NO_DISPLAY && eglSurface != EGL_NO_SURFACE) {
        eglDestroySurface(eglDisplay, eglSurface);
        eglSurface = EGL_NO_SURFACE;
    }
    eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, window, NULL);
    if (eglSurface == EGL_NO_SURFACE) {
        return ;
    }
    if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
        return ;
    }

}
int EglHelper::changeSurface(EGLNativeWindowType window) {
    if (eglDisplay != EGL_NO_DISPLAY) {
        eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }

    if (eglDisplay != EGL_NO_DISPLAY && eglSurface != EGL_NO_SURFACE) {
        eglDestroySurface(eglDisplay, eglSurface);
        eglSurface = EGL_NO_SURFACE;
    }
    eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, window, NULL);
    if (eglSurface == EGL_NO_SURFACE) {
        return -1;
    }
    if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
        return -1;
    }
    return  0;
}

int EglHelper::swapBuffers() {
    if (eglDisplay != EGL_NO_DISPLAY && eglSurface != EGL_NO_SURFACE) {
        if (eglSwapBuffers(eglDisplay, eglSurface)) {
            return 0;
        }
    }
    return -1;
}

void EglHelper::destroyEgl() {
    if (eglDisplay != EGL_NO_DISPLAY) {
        eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
    if (eglDisplay != EGL_NO_DISPLAY && eglSurface != EGL_NO_SURFACE) {
        eglDestroySurface(eglDisplay, eglSurface);
        eglSurface = EGL_NO_SURFACE;
    }
    if (eglDisplay != EGL_NO_DISPLAY && eglContext != EGL_NO_CONTEXT) {
        eglDestroyContext(eglDisplay, eglContext);
        eglContext = EGL_NO_CONTEXT;
    }
    if (eglDisplay != EGL_NO_DISPLAY) {
        eglTerminate(eglDisplay);
        eglDisplay = EGL_NO_DISPLAY;
    }

}

