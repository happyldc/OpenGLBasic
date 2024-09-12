package com.example.demo.base.egl;

import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * 2D纹理图绘制
 *
 * @author HB.LDC
 * @date 2024-09-07
 */
public class SimpleTextureEGLHelper extends EGLHelper {
    protected int vertexShader;
    protected int fragmentShader;
    protected int program;

    protected int positionHandle;
    protected int texturePositionHandle;
    protected int samplerHandler;


    private final float[] vertexData = {
            // 顶点坐标 (x, y, z)
            1.0f, 1.0f,   // 右上角的点

            -1.0f, 1.0f,   // 公共边顶点1 左上角
            1.0f, -1.0f,   // 公共边顶点2 右上角

            -1.0f, -1.0f   //左下角的点

    };
    //纹理坐标，一整个纹理图，要绘制出来的区域范围的点
    //左上角(0,0) 右上角（1.0）
    //左下角(0,1) 右上角（1.1）
    //纹理坐标值的范围 是0～1
    private final float[] textureData = {
            // 顶点坐标 (x, y)
            1.0f, 0.0f,   // 右上角的点

            0.0f, 0.0f,   // 公共边顶点1 左上角
            1.0f, 1.0f,   // 公共边顶点2 右上角

            0.0f, 1.0f   //左下角的点
    };

    private float[] triangleColor = {new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0f};  // 默认红色

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    public SimpleTextureEGLHelper() {
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

    private int textureId;

    @Override
    public void initShaders() {
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Shaders.Companion.getTexture_vertex_shader());
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.Companion.getTexture_fragment_shader());
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        texturePositionHandle = GLES20.glGetAttribLocation(program, "f_Position");//纹理坐标
        samplerHandler = GLES20.glGetUniformLocation(program, "sTexture");//2d纹理图

        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        textureId = texture[0];
        //绑定纹理 textureId为0时 解绑纹理 ，需要操作一个纹理就需要bindTexture一次
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //横、纵坐标环绕方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

    }

    @Override
    public void render() {
        triangleColor[0] = new Random().nextFloat();
        triangleColor[1] = new Random().nextFloat();
        triangleColor[2] = new Random().nextFloat();

        ensureOutSurfacesCreated();

        //使用程序
        GLES20.glUseProgram(program);
        // 启用顶点属性并传递顶点数据
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);
        //size：每个点的有几个元素 这里3个坐标值代表1个点
        //type:点的数值类型 float
        //normalized: 是否归一化处理。如果点顶点坐标值已经按openGL坐标[0,1]处理过了传false
        //              如果是按屏幕坐标传的要传true
        //stride：跨度 点和点直接的间隔  1个点2个坐标（x,y）,一个float占4个字节 则跨度为8
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
        //默认激活第0个 总的有GL_TEXTURE31个
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
//        GLES20.glUniform1i(texturePositionHandle,5);

        //提交纹理图方式1
         GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureImgWidth, textureImgHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, textureImgBuffer);
        //提交纹理图方式2 保存bitmap纹理对象 使用android自带的api
        //GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap,0);
        // 渲染(绘制)
        for (GLSurface output : outputSurfaces) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            // 设置当前的上下文环境和输出缓冲区
            EGL14.eglMakeCurrent(eglDisplay, output.eglSurface, output.eglSurface, eglContext);
            // 设置视窗大小及位置
            GLES20.glViewport(output.viewport.x, output.viewport.y, output.viewport.width, output.viewport.height);
            // 绘制
            onDrawFrame(output);
            // 交换显存(将surface显存和显示器的显存交换)
            EGL14.eglSwapBuffers(eglDisplay, output.eglSurface);
        }
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texturePositionHandle);
    }


    private void onDrawFrame(GLSurface output) {

        //绘制三角形
        //mode 绘制图形的类型
        //first 从第几个点开始绘制
        //绘制点的个数 3个
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }


    @Override
    public void destroy() {
        // 销毁eglSurface
        for (GLSurface outputSurface : outputSurfaces) {
            EGL14.eglDestroySurface(eglDisplay, outputSurface.eglSurface);
            outputSurface.eglSurface = EGL14.EGL_NO_SURFACE;
        }
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        eglContext = EGL14.EGL_NO_CONTEXT;
        eglDisplay = EGL14.EGL_NO_DISPLAY;
    }

    int textureImgWidth;
    int textureImgHeight;
    Buffer textureImgBuffer;

    public void setTextureBitmap(@Nullable Bitmap bitmap) {
        textureImgWidth = bitmap.getWidth();
        textureImgHeight = bitmap.getHeight();
        textureImgBuffer = ByteBuffer.allocate(bitmap.getByteCount());//bitmap.getByteCount()= (bitmap.getWidth() * bitmap.getHeight() * 4)
        bitmap.copyPixelsToBuffer(textureImgBuffer);
        textureImgBuffer.flip();//这个方法一定要调用 不然渲染不了

    }
}
