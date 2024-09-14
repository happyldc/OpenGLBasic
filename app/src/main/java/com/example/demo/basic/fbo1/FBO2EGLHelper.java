package com.example.demo.basic.fbo1;

import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.demo.base.egl.EGLHelper;
import com.example.demo.base.egl.GLSurface;
import com.example.demo.base.egl.IReadBitmapCallback;
import com.example.demo.base.egl.Shaders;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * fbo1
 *
 * @author HB.LDC
 * @date 2024-09-12
 */
public class FBO2EGLHelper extends EGLHelper {
    protected int vertexShader;
    protected int fragmentShader;
    protected int program;

    protected int positionHandle;
    protected int texturePositionHandle;
    protected int samplerHandler;
    //三角形顶点坐标
    private final float[] vertexData = {
            1f, 1f,
            -1f, 1f,
            1f, -1f,
            -1f, -1f
    };
    //三角形顶点buffer
    private FloatBuffer vertexBuffer;

    private final float[] textureData = {
            1f, 1f,
            0f, 1f,
            1f, 0f,
            0f, 0f
    };
    private FloatBuffer textureBuffer;


    private DrawTextureProgram drawTextureProgram = new DrawTextureProgram();
    private IReadBitmapCallback readBitmapCallback;

    public void setReadBitmapCallback(IReadBitmapCallback readBitmapCallback) {
        this.readBitmapCallback = readBitmapCallback;
    }

    public FBO2EGLHelper() {
        // 将顶点数据存储到缓冲区中
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);

        //纹理顶点
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureData.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        textureBuffer = tbb.asFloatBuffer();
        textureBuffer.put(textureData);
        textureBuffer.position(0);
    }

    private int width = 720;
    private int height = 1280;

    private int fboTextureId = 0;
    private int renderTexture = 0;

    @Override
    public void initShaders() {
        //绘制三角形的顶点
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Shaders.Companion.getFbo1_texture_vertex_shader());
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.Companion.getFbo1_texture_fragment_shader());
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        texturePositionHandle = GLES20.glGetUniformLocation(program, "aTexCoord");
        samplerHandler = GLES20.glGetUniformLocation(program, "sTexture");
        renderTexture = createRenderTexture();
        fboTextureId = createFBO(width, height);

        drawTextureProgram.initShaders();
        drawTextureProgram.setRotation(180);
    }

    private Bitmap textureBitmap;

    public void setBitmap(Bitmap textureBitmap) {
        this.textureBitmap = textureBitmap;
    }

    private int createRenderTexture() {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }

    private int createFBO(int width, int height) {
        //创建并绑定FrameBuffer
        int[] frameBuffer = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

        //创建纹理 并绑定到FBO
        int textureId = createTexture(width, height);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //将纹理附加到 Framebuffer的颜色附件
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
        //检查FrameBuffer 是否绑定正确
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete");
        }
        //解绑FBO 回到默认的FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //返回纹理Id 稍后用于绘制
        return textureId;
    }

    private int createTexture(int width, int height) {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        // 设置纹理参数
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // 分配纹理存储空间
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return texture[0];
    }

    @Override
    public void render() {
        if (textureBitmap == null) {
            return;
        }
        GLES20.glViewport(0, 0, width, height);

        drawTextureToFBO(fboTextureId);


        for (GLSurface output : outputSurfaces) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            // 设置当前的上下文环境和输出缓冲区
            EGL14.eglMakeCurrent(eglDisplay, output.getEglSurface(), output.getEglSurface(), eglContext);
            // 设置视窗大小及位置
            GLES20.glViewport(output.getViewport().x, output.getViewport().y, output.getViewport().width, output.getViewport().height);
            // 绘制
            drawFBOToSurface(output, fboTextureId);
            // 交换显存(将surface显存和显示器的显存交换)
            EGL14.eglSwapBuffers(eglDisplay, output.getEglSurface());
        }

    }

    private void drawTextureToFBO(int fboTextureId) {
        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboTextureId);
        //设置清除的颜色
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //使用三角形shader程序
        GLES20.glUseProgram(program);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

        GLES20.glEnableVertexAttribArray(texturePositionHandle);
        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
        //将bitmap数据加载到纹理中
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);

        GLES20.glUniform1i(samplerHandler, 0);
        //绘制到FBO
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texturePositionHandle);
        if (readBitmapCallback != null) {
            ByteBuffer bitmapBuffer = ByteBuffer.allocateDirect(width * height * 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmapBuffer);
            readBitmapCallback.onRead(bitmapBuffer, width, height);
        }
        // 解绑 FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void drawFBOToSurface(GLSurface output, int textureId) {
        drawTextureProgram.render(textureId);
    }


    @Override
    public void destroy() {
        drawTextureProgram.destroy();
    }


}
