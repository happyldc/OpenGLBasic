#include <jni.h>
#include <android/bitmap.h>
#include <string>
#include <android/log.h>

extern "C"
JNIEXPORT jstring JNICALL Java_com_example_jni_JNITest_getStringFromNative(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_jni_JNITest_applyMosaicEffectNative(JNIEnv *env, jobject clazz, jobject bitmap, jint blockSize) {
    AndroidBitmapInfo info;
    void* pixels;
    int ret;

    // 获取Bitmap信息
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        return nullptr;
    }

    // 锁定Bitmap像素以供读取和写入
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        return nullptr;
    }

    // 这里假设Bitmap是ARGB_8888格式
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        // 处理或返回错误，因为我们的示例仅支持RGBA_8888
        AndroidBitmap_unlockPixels(env, bitmap);
        return nullptr;
    }

    // 遍历Bitmap并应用马赛克效果
    uint32_t* line = (uint32_t*)pixels;
    int width = info.width;
    int height = info.height;

    for (int y = 0; y < height; y += blockSize) {
        for (int x = 0; x < width; x += blockSize) {
            uint32_t baseColor = line[x + y * width]; // 假设第一个像素代表整个块的颜色

            // 遍历小块内的像素
            for (int dy = 0; dy < blockSize && y + dy < height; dy++) {
                for (int dx = 0; dx < blockSize && x + dx < width; dx++) {
                    int index = (x + dx) + (y + dy) * width;
                    line[index] = baseColor; // 设置颜色
                }
            }
        }
    }

    // 解锁Bitmap像素
    AndroidBitmap_unlockPixels(env, bitmap);

    // 直接返回原始Bitmap对象，因为它已经被修改
    return bitmap;
}





void swapYU12toYUV420SP(const jbyte* yu12bytes, jbyte* i420bytes, int width, int height, int yStride, int uStride, int vStride) {
    memcpy(i420bytes, yu12bytes, yStride * height);
    int startPos = yStride * height;
    int yv_start_pos_u = startPos;
    int yv_start_pos_v = startPos + startPos / 4;
    for (int i = 0; i < startPos / 4; i++) {
        i420bytes[startPos + 2 * i + 0] = yu12bytes[yv_start_pos_v + i];
        i420bytes[startPos + 2 * i + 1] = yu12bytes[yv_start_pos_u + i];
    }
}



// JNI 方法声明
extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_jni_JNITest_i420ToBitmap(JNIEnv *env, jclass clazz,
                                          jint width, jint height, jint rotation,
                                          jint bufferLength, jbyteArray buffer,
                                          jint yStride, jint uStride, jint vStride,jint nv21Format) {
    jbyte* yu12bytes = env->GetByteArrayElements(buffer, NULL);
    jbyte* nv21 = (jbyte*) malloc(bufferLength);

    // 调用 swapYU12toYUV420SP 方法
    swapYU12toYUV420SP(yu12bytes, nv21, width, height, yStride, uStride, vStride);

    // 准备 YuvImage 对象
    jclass yuvImageClass = env->FindClass("android/graphics/YuvImage");
    jmethodID yuvImageCtor = env->GetMethodID(yuvImageClass, "<init>", "([BIII[I)V");
    jintArray strides = env->NewIntArray(2);
    jint strideValues[] = {yStride, yStride};
    env->SetIntArrayRegion(strides, 0, 2, strideValues);
    jobject yuvImage = env->NewObject(yuvImageClass, yuvImageCtor, env->NewByteArray(bufferLength), nv21Format, width, height, strides);

    // 调用 compressToJpeg 方法
    jmethodID compressToJpegMethod = env->GetMethodID(yuvImageClass, "compressToJpeg", "(Landroid/graphics/Rect;ILjava/io/OutputStream;)Z");
    jclass rectClass = env->FindClass("android/graphics/Rect");
    jmethodID rectCtor = env->GetMethodID(rectClass, "<init>", "(IIII)V");
    jobject rect = env->NewObject(rectClass, rectCtor, 0, 0, width, height);

    jclass byteArrayOutputStreamClass = env->FindClass("java/io/ByteArrayOutputStream");
    jmethodID byteArrayOutputStreamCtor = env->GetMethodID(byteArrayOutputStreamClass, "<init>", "()V");
    jobject byteArrayOutputStream = env->NewObject(byteArrayOutputStreamClass, byteArrayOutputStreamCtor);

    env->CallBooleanMethod(yuvImage, compressToJpegMethod, rect, 100, byteArrayOutputStream);

    // 旋转图片
    jclass matrixClass = env->FindClass("android/graphics/Matrix");
    jmethodID matrixCtor = env->GetMethodID(matrixClass, "<init>", "()V");
    jobject matrix = env->NewObject(matrixClass, matrixCtor);
    jmethodID postRotateMethod = env->GetMethodID(matrixClass, "postRotate", "(F)Z");
    env->CallBooleanMethod(matrix, postRotateMethod, (jfloat)rotation);

    // 获取 JPEG 数据并转换为 Bitmap
    jmethodID toByteArrayMethod = env->GetMethodID(byteArrayOutputStreamClass, "toByteArray", "()[B");
    jbyteArray jpegData = (jbyteArray)env->CallObjectMethod(byteArrayOutputStream, toByteArrayMethod);

    jclass bitmapFactoryClass = env->FindClass("android/graphics/BitmapFactory");
    jmethodID decodeByteArrayMethod = env->GetStaticMethodID(bitmapFactoryClass, "decodeByteArray", "([BII)Landroid/graphics/Bitmap;");
    jobject bitmap = env->CallStaticObjectMethod(bitmapFactoryClass, decodeByteArrayMethod, jpegData, 0, env->GetArrayLength(jpegData));

    // 释放资源
    env->ReleaseByteArrayElements(buffer, yu12bytes, 0);
    free(nv21);

    return bitmap;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jni_JNITest_swapYU12toYUV420SP(JNIEnv *env, jclass clazz,
                                                jbyteArray yu12bytes, jbyteArray i420bytes,
                                                jint width, jint height,
                                                jint yStride, jint uStride, jint vStride) {
    jbyte* yu12 = env->GetByteArrayElements(yu12bytes, NULL);
    jbyte* i420 = env->GetByteArrayElements(i420bytes, NULL);

    // 调用原始的 C++ 函数
    swapYU12toYUV420SP(yu12, i420, width, height, yStride, uStride, vStride);

    // 释放 Java 数组元素
    env->ReleaseByteArrayElements(yu12bytes, yu12, 0);
    env->ReleaseByteArrayElements(i420bytes, i420, 0);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_jni_JNITest_applyMosaicToYUVNative(JNIEnv *env, jobject thiz, jbyteArray yuv_data, jint width, jint height, jint mosaic_size) {
    jbyte *yuvData = env->GetByteArrayElements(yuv_data, NULL);
    int frameSize = width * height;
    int mosaicSize = mosaic_size;

    // 遍历Y分量，应用马赛克效果
    for (int y = 0; y < height; y += mosaicSize) {
        for (int x = 0; x < width; x += mosaicSize) {
            int sumY = 0;
            int count = 0;
            for (int dy = 0; dy < mosaicSize && y + dy < height; dy++) {
                for (int dx = 0; dx < mosaicSize && x + dx < width; dx++) {
                    int index = (y + dy) * width + (x + dx);
                    sumY += yuvData[index] & 0xFF;
                    count++;
                }
            }
            int avgY = sumY / count;

            // 将亮度值应用到这个区域
            for (int dy = 0; dy < mosaicSize && y + dy < height; dy++) {
                for (int dx = 0; dx < mosaicSize && x + dx < width; dx++) {
                    int index = (y + dy) * width + (x + dx);
                    yuvData[index] = static_cast<jbyte>(avgY);
                }
            }
        }
    }

    // UV分量处理，UV分辨率是Y的一半，所以需要按2x2的区域进行处理
    for (int y = 0; y < height / 2; y += mosaicSize / 2) {
        for (int x = 0; x < width / 2; x += mosaicSize / 2) {
            int uvIndex = frameSize + (y * width) + (x * 2);

            // 采集UV分量
            jbyte u = yuvData[uvIndex];
            jbyte v = yuvData[uvIndex + 1];

            // 应用UV分量到区域
            for (int dy = 0; dy < mosaicSize / 2 && (y + dy) < height / 2; dy++) {
                for (int dx = 0; dx < mosaicSize / 2 && (x + dx) < width / 2; dx++) {
                    int targetUVIndex = frameSize + ((y + dy) * width) + (x + dx) * 2;
                    yuvData[targetUVIndex] = u;
                    yuvData[targetUVIndex + 1] = v;
                }
            }
        }
    }

    // 释放数组
    env->ReleaseByteArrayElements(yuv_data, yuvData, 0);
}

//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_jni_JNITest_convertYUVtoRGB(JNIEnv *env, jobject instance, jbyteArray yuv_, jint width, jint height, jintArray rgb_) {
//    jbyte *yuv = env->GetByteArrayElements(yuv_, NULL);
//    jint *rgb = env->GetIntArrayElements(rgb_, NULL);
//
//    int frameSize = width * height;
//    int yIndex = 0;
//    int uvIndex = frameSize;
//    int u = 0, v = 0, y1192 = 0;
//    int r, g, b;
//
//    for (int j = 0; j < height; j++) {
//        int uvp = uvIndex + (j >> 1) * width;
//        int yp = yIndex + j * width;
//        for (int i = 0; i < width; i++, yp++) {
//            int y = (0xff & ((int) yuv[yp])) - 16;
//            if (y < 0) y = 0;
//            if ((i & 1) == 0) {
//                v = (0xff & yuv[uvp++]) - 128;
//                u = (0xff & yuv[uvp++]) - 128;
//            }
//
//            y1192 = 1192 * y;
//            r = (y1192 + 1634 * v);
//            g = (y1192 - 833 * v - 400 * u);
//            b = (y1192 + 2066 * u);
//
//            if (r < 0) r = 0;
//            else if (r > 262143) r = 262143;
//            if (g < 0) g = 0;
//            else if (g > 262143) g = 262143;
//            if (b < 0) b = 0;
//            else if (b > 262143) b = 262143;
//
//            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
//        }
//    }
//
//    env->ReleaseByteArrayElements(yuv_, yuv, 0);
//    env->ReleaseIntArrayElements(rgb_, rgb, 0);
//}




