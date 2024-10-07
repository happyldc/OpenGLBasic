package com.example.demo.basic.fbo1;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.demo.base.egl.Shaders;
import com.example.demo.basic.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-13
 */
public class DrawTextureProgram {
    int vertexShader;
    int fragmentShader;
    int program;

    int positionHandle;
    int texturePositionHandle;
    int samplerHandler;
    int matrixHandler;


    private final float[] vertexData = {
//            -1.0f, 1.0f,   // 左上
//            -1.0f, -1.0f,   // 左下
//            1.0f, 1.0f,   // 右上
//            1.0f, -1.0f,   // 右下
            1f, 1f,
            -1f, 1f,
            1f, -1f,
            -1f, -1f


    };
    //纹理坐标，一整个纹理图，要绘制出来的区域范围的点
    //左上角(0,0) 右上角（1.0）
    //左下角(0,1) 右上角（1.1）
    //纹理坐标值的范围 是0～1
    private final float[] textureData = {
//            0.0f, 1.0f,  // 左上
//            0.0f, 0.0f,  // 左下
//            1.0f, 1.0f,  // 右上
//            1.0f, 0.0f   // 右下
            1f, 1f,
            0f, 1f,
            1f, 0f,
            0f, 0f

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
        vertexShader = GLUtils.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.Companion.getMatrix_texture_vertex_shader());
        fragmentShader = GLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.Companion.getMatrix_texture_fragment_shader());
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
        matrixHandler = GLES20.glGetUniformLocation(program, "uMatrix");
        samplerHandler = GLES20.glGetUniformLocation(program, "sTexture");
        setRotation(0);

    }

    float[] rotationMatrix = new float[16];

    public void setRotation(int a) {
        Matrix.setRotateM(rotationMatrix, 0, a, 0, 0, 1);

    }

    public void render(int textureId) {
        render(GLES20.GL_TEXTURE_2D, textureId);
    }

    public void render(int textureType, int textureId) {
        GLES20.glUseProgram(program);
        // 将旋转矩阵传递给着色器
        GLES20.glUniformMatrix4fv(matrixHandler, 1, false, rotationMatrix, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

        GLES20.glEnableVertexAttribArray(texturePositionHandle);
        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

        //绑定FBO生成的纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(textureType, textureId);

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