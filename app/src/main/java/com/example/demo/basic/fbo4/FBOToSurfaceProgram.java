package com.example.demo.basic.fbo4;

import android.opengl.GLES20;

import com.example.demo.basic.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-15
 */
public class FBOToSurfaceProgram {
    int vertexShader;
    int fragmentShader;
    int program;

    int positionHandle;
    int texturePositionHandle;
    int samplerHandler;
    int matrixHandler;
    private final float[] vertexData = {
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

            1f, 1f,
            0f, 1f,
            1f, 0f,
            0f, 0f

    };


    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    public FBOToSurfaceProgram() {
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


    public void init() {
        vertexShader = GLUtils.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.INSTANCE.getOes_texture_vertex_shader());
        fragmentShader = GLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.INSTANCE.getFbo_to_surface_fragment_shader());
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        texturePositionHandle = GLES20.glGetAttribLocation(program, "texCoord");
        samplerHandler = GLES20.glGetUniformLocation(program, "fboTexture");

    }

    public void drawToScreen(int fboTextureId) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        // 使用屏幕渲染的着色器程序
        GLES20.glUseProgram(program);

        // 绑定 FBO 生成的纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(samplerHandler, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);
        // 设置顶点和纹理坐标数据
        vertexBuffer.position(0);  // 顶点位置
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

        vertexBuffer.position(0);  // 纹理坐标
        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

        // 绘制四边形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texturePositionHandle);
    }
}
