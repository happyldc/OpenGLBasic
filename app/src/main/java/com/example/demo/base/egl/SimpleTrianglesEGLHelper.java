package com.example.demo.base.egl;

import android.opengl.EGL14;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * 三角形绘制
 *
 * @author HB.LDC
 * @date 2024-09-07
 */
public class SimpleTrianglesEGLHelper extends EGLHelper {
    protected int vertexShader;
    protected int fragmentShader;
    protected int program;

    protected int positionHandle;
    protected int colorHandle;


    private final float[] vertexData = {
            // 顶点坐标 (x, y, z)
            0.0f, 0.5f, 0.0f,   // 顶点1
            -0.5f, -0.5f, 0.0f,   // 顶点2
            0.5f, -0.5f, 0.0f    // 顶点3
    };
    private float[] triangleColor = {new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0f};  // 默认红色

    private FloatBuffer vertexBuffer;

    public SimpleTrianglesEGLHelper() {
        // 将顶点数据存储到缓冲区中
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
    }

    @Override
    public void initShaders() {
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Shaders.Companion.getSimple_vertex_shader());
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.Companion.getSimple_fragment_shader());
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        colorHandle = GLES20.glGetUniformLocation(program, "u_Color");
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void render() {
        triangleColor[0] = new Random().nextFloat();
        triangleColor[1] = new Random().nextFloat();
        triangleColor[2] = new Random().nextFloat();

        ensureOutSurfacesCreated();
        //使用程序
        GLES20.glUseProgram(program);
        // 设置颜色
        GLES20.glUniform4fv(colorHandle, 1, triangleColor, 0);
        // 启用顶点属性并传递顶点数据
        GLES20.glEnableVertexAttribArray(positionHandle);
        //size：每个点的有几个元素 这里3个坐标值代表1个点
        //type:点的数值类型 float
        //normalized: 是否归一化处理。如果点顶点坐标值已经按openGL坐标[0,1]处理过了传false
        //              如果是按屏幕坐标传的要传true
        //stride：跨度 点和点直接的间隔  todo 待确认 错误 1个点2个坐标（x,y）,一个float占4个字节 则跨度为8
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);


        // 渲染(绘制)
        for (GLSurface output : outputSurfaces) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
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
    }


    private void onDrawFrame(GLSurface output) {

        //绘制三角形
        //mode 绘制图形的类型
        //first 从第几个点开始绘制
        //绘制点的个数 3个
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

    }
//    private void onDrawFrame(GLSurface output) {
//
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        GLES20.glUseProgram(program);
//        // 设置颜色
//        GLES20.glUniform4fv(colorHandle, 1, triangleColor, 0);
//        // 启用顶点属性并传递顶点数据
//        GLES20.glEnableVertexAttribArray(positionHandle);
//        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//
//        //绘制三角形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
//        GLES20.glDisableVertexAttribArray(positionHandle);
//    }



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



}
