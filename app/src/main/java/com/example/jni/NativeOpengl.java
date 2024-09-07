package com.example.jni;

import android.view.Surface;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-08-27
 */
public class NativeOpengl {
    static {
        System.loadLibrary("native-lib");
    }

    public  native  void surfaceCreate(Surface surface);

    /***
     * 会崩溃
     * @param surface
     */
    public  native  void changeSurface(Surface surface);
    public  native  void onSizeChanged(int width,int height);
    public  native  void drawColor(float r,float g,float b);
}
