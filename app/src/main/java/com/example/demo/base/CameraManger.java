package com.example.demo.base;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 摄像机管理类
 *
 * @author HB.LDC
 * @date 2024-08-21
 */
public class CameraManger {
    private Context mContext;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    public void init(Context context) {
        this.mContext = context;
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        setDefaultParameters();
    }

    public void setDefaultParameters() {
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG); //图片输出格式
        mCamera.setParameters(mParameters);
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPreviewDisplay(SurfaceTexture surfaceTexture) {
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startPreview() {
        mCamera.startPreview();
    }

    public void stopPreview() {
        mCamera.stopPreview();
    }



}
