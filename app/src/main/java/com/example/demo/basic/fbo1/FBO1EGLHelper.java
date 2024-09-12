package com.example.demo.basic.fbo1;

import android.opengl.EGL14;
import android.opengl.GLES20;

import com.example.demo.base.egl.EGLHelper;
import com.example.demo.base.egl.GLSurface;
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
public class FBO1EGLHelper extends EGLHelper {
    protected int vertexShader;
    protected int fragmentShader;
    protected int triangleProgram;

    protected int positionHandle;
    protected int colorHandle;
    //三角形顶点坐标
    private final float[] vertexData = {
            // 顶点坐标 (x, y, z)
            0.0f, 1f, 0.0f,   // 顶点1
            -1f, -1f, 0.0f,   // 顶点2
            1f, -1f, 0.0f    // 顶点3
    };
    //三角形顶点buffer
    private FloatBuffer vertexBuffer;

    //三角形颜色
    private float[] triangleColor = {new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0f};  // 默认红色

    private DrawTextureProgram drawTextureProgram = new DrawTextureProgram();

    public FBO1EGLHelper() {
        // 将顶点数据存储到缓冲区中
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
    }

    private int width = 720;
    private int height = 1280;

    private int fboTextureId = 0;

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void initShaders() {
        //绘制三角形的顶点
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Shaders.Companion.getSimple_vertex_shader());
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.Companion.getSimple_fragment_shader());
        triangleProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(triangleProgram, vertexShader);
        GLES20.glAttachShader(triangleProgram, fragmentShader);
        GLES20.glLinkProgram(triangleProgram);
        positionHandle = GLES20.glGetAttribLocation(triangleProgram, "a_Position");
        colorHandle = GLES20.glGetUniformLocation(triangleProgram, "u_Color");
        fboTextureId = createFBO(width, height);
        drawTextureProgram.initShaders();
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

        return texture[0];
    }

    @Override
    public void render() {
        GLES20.glViewport(0, 0, width, height);

        drawTriangleToFBO(fboTextureId);
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

    private void drawFBOToSurface(GLSurface output, int textureId) {
        drawTextureProgram.render(textureId);
    }

    private void drawTriangleToFBO(int fboTextureId) {
        triangleColor[0] = new Random().nextFloat();
        triangleColor[1] = new Random().nextFloat();
        triangleColor[2] = new Random().nextFloat();

        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboTextureId);
        //设置清除的颜色
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //使用三角形shader程序
        GLES20.glUseProgram(triangleProgram);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glUniform4fv(colorHandle, 1, triangleColor, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDisableVertexAttribArray(positionHandle);
        //解绑FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

    }

    @Override
    public void destroy() {

    }

    class DrawTextureProgram {
        int vertexShader;
        int fragmentShader;
        int program;

        int positionHandle;
        int texturePositionHandle;
        int samplerHandler;


        private final float[] vertexData = {
                -1.0f, 1.0f,   // 左上
                -1.0f, -1.0f,   // 左下
                1.0f, 1.0f,   // 右上
                1.0f, -1.0f,   // 右下

        };
        //纹理坐标，一整个纹理图，要绘制出来的区域范围的点
        //左上角(0,0) 右上角（1.0）
        //左下角(0,1) 右上角（1.1）
        //纹理坐标值的范围 是0～1
        private final float[] textureData = {
                0.0f, 1.0f,  // 左上
                0.0f, 0.0f,  // 左下
                1.0f, 1.0f,  // 右上
                1.0f, 0.0f   // 右下
        };


        private FloatBuffer vertexBuffer;
        private FloatBuffer textureBuffer;

        public DrawTextureProgram() {
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


        public void initShaders() {
            vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Shaders.Companion.getFbo1_texture_vertex_shader());
            fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.Companion.getFbo1_texture_fragment_shader());
            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
            positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
            texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
            samplerHandler = GLES20.glGetUniformLocation(program, "sTexture");

        }

        public void render(int textureId) {
            GLES20.glUseProgram(program);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

            GLES20.glEnableVertexAttribArray(texturePositionHandle);
            GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

            //绑定FBO生成的纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

            GLES20.glUniform1i(samplerHandler, 0);

            //绘制纹理到屏幕上
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            // 禁用顶点和纹理属性
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(texturePositionHandle);
        }

        public void destroy() {

        }

    }
}
