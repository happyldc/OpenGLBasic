package com.example.demo.basic.fbo4;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.demo.base.egl.EGLHelper;
import com.example.demo.base.egl.GLSurface;
import com.example.demo.base.egl.Shaders;
import com.example.demo.basic.fbo1.DrawTextureProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * fbo1
 * oes ->texture2D ->Surface
 *
 * @author HB.LDC
 * @date 2024-09-12
 */
public class FBOCameraEGLHelper extends EGLHelper {


    private DrawTextureProgram drawTextureProgram = new DrawTextureProgram();
    private CameraRender cameraRender = new CameraRender();

    private OESToFBOProgram oesToFBOProgram = new OESToFBOProgram();
//    private FBOToSurfaceProgram fboToSurfaceProgram = new FBOToSurfaceProgram();

    public FBOCameraEGLHelper() {
        drawTextureProgram.setRotation(0);

    }

    public SurfaceTexture getSurfaceTexture() {
        return cameraRender.getSurfaceTexture();
    }

    private int width = 720;
    private int height = 1280;
    int fboTextureId;
    int frameBufferId;

    @Override
    public void initShaders() {
        cameraRender.init();
        oesToFBOProgram.init();
//        fboToSurfaceProgram.init();
        //绘制三角形的顶点
        int[] frameBuffer = new int[1];
        fboTextureId = createEmptyTexture2DBindFrameBuffer(frameBuffer, width, height);
        frameBufferId = frameBuffer[0];
        drawTextureProgram.initShaders();
        drawTextureProgram.setRotation(-90);
    }

    public static int createEmptyTexture2DBindFrameBuffer(int[] frameBuffer, int texPixWidth, int texPixHeight) {
        // 创建纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        // 绑定GL_TEXTURE_2D纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        // 纹理采样
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // 创建一个空的2D纹理对象，指定其基本参数，并绑定到对应的纹理ID上
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texPixWidth, texPixHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        // 取消绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);


        // 创建帧缓冲区
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        // 将帧缓冲对象绑定到OpenGL ES上下文的帧缓冲目标上
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        // 使用GLES20.GL_COLOR_ATTACHMENT0将纹理作为颜色附着点附加到帧缓冲对象上
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
        // 取消绑定缓冲区
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);

        return textures[0];
    }


    @Override
    public void render() {
        if (getSurfaceTexture() == null) {
            return;
        }
        GLES20.glViewport(0, 0, width, height);

        getSurfaceTexture().updateTexImage();
        oesToFBOProgram.drawToFramebuffer(cameraRender.getTextureId(), frameBufferId);

        for (GLSurface output : outputSurfaces) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            // 设置当前的上下文环境和输出缓冲区
            EGL14.eglMakeCurrent(eglDisplay, output.getEglSurface(), output.getEglSurface(), eglContext);
            // 设置视窗大小及位置
            GLES20.glViewport(output.getViewport().x, output.getViewport().y, output.getViewport().width, output.getViewport().height);
            // 绘制
            drawTextureProgram.render(fboTextureId);
//            fboToSurfaceProgram.drawToScreen(fboTextureId);
            // 交换显存(将surface显存和显示器的显存交换)
            EGL14.eglSwapBuffers(eglDisplay, output.getEglSurface());
        }

    }


    @Override
    public void destroy() {
        drawTextureProgram.destroy();
    }


}
