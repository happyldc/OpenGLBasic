#include <jni.h>
#include <android/bitmap.h>
#include <string>
#include <android/log.h>
#include "egl/EglHelper.h"
#include "EGL/egl.h"
#include "GLES2/gl2.h"
#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "log/AndroidLog.h"
#include "egl/EglHelper.h"


EglHelper *eglHelper = NULL;
ANativeWindow *nativeWindow = NULL;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_jni_NativeOpengl_surfaceCreate(JNIEnv *env, jobject thiz, jobject surface) {
    nativeWindow = ANativeWindow_fromSurface(env, surface);
    eglHelper = new EglHelper();
    eglHelper->initEgl(nativeWindow);

    ///OpenGL 代码
    //当前模拟器屏幕的大小
    glViewport(0, 0, 720, 1280);
    //用color 清屏
    glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
    //清除color buffer
    glClear(GL_COLOR_BUFFER_BIT);

    eglHelper->swapBuffers();

}
ANativeWindow *newWindow;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_jni_NativeOpengl_changeSurface(JNIEnv *env, jobject thiz, jobject surface) {

    newWindow = ANativeWindow_fromSurface(env, surface);
    eglHelper->changeSurface(newWindow);
    ANativeWindow_release(nativeWindow);
    nativeWindow = newWindow;


}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jni_NativeOpengl_onSizeChanged(JNIEnv *env, jobject thiz, jint width,
                                                jint height) {
    glViewport(0, 0, width, height);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_jni_NativeOpengl_drawColor(JNIEnv *env, jobject thiz, jfloat r, jfloat g, jfloat b) {
    //用color 清屏
    glClearColor(r, g, b, 1.0f);
    //清除color buffer
    glClear(GL_COLOR_BUFFER_BIT);

    eglHelper->swapBuffers();
}