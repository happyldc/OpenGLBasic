package com.example.jni;

import android.graphics.Bitmap;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-08-12
 */
public class JNITest {
    static {
        System.loadLibrary("native-lib");
    }


//    public native String getStringFromNative();

    /**
     * 将Bitmap处理为马赛克效果
     *
     * @param source    原始Bitmap的引用（通过JNI传递）
     * @param blockSize 马赛克块的大小（如20）
     * @return 处理后的Bitmap的引用（通过JNI返回）
     */
    public Bitmap applyMosaicEffect(Bitmap source, int blockSize) {
        return applyMosaicEffectNative(source, blockSize);
    }

    // 声明native方法
    private native Bitmap applyMosaicEffectNative(Bitmap source, int blockSize);


    public  native Bitmap i420ToBitmap(int width, int height, int rotation,
                                             int bufferLength, byte[] buffer,
                                             int yStride, int uStride, int vStride,int nv21Format);

    // JNI 方法声明
    public static native void swapYU12toYUV420SP(byte[] yu12bytes, byte[] i420bytes,
                                                 int width, int height,
                                                 int yStride, int uStride, int vStride);

    public native void applyMosaicToYUVNative(byte[] yuvData, int width, int height, int mosaicSize);
//    public native void convertYUVtoRGB(byte[] yuvData, int width, int height, int[] rgbData);

}
