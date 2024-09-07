//
// Created by lindiancheng on 2024/8/27.
//

#ifndef TQDEMO_EGLHELPER_H
#define TQDEMO_EGLHELPER_H

//openGL 环境
#include "Egl/egl.h"

class EglHelper {
public:
//默认初始化
    EGLDisplay eglDisplay = EGL_NO_DISPLAY;
    EGLSurface eglSurface = EGL_NO_SURFACE;
    EGLContext eglContext = EGL_NO_CONTEXT;
    EGLConfig eglConfig = NULL;
public:
    EglHelper();

    ~EglHelper();

    ///成功返回 0 不成功返回-1
    int initEgl(EGLNativeWindowType window);

    ///成功返回 0 不成功返回-1
    int changeSurface(EGLNativeWindowType window);

    ///成功返回0 失败返回-1
    int swapBuffers();

    ///销毁
    void destroyEgl();
    ///重新绑定当前surface
    void attachSurface(EGLNativeWindowType window);

};


#endif //TQDEMO_EGLHELPER_H
