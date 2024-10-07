package com.example.demo.basic.camera;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-10-07
 */
public class CameraTexture {

    private static final String TAG = CameraTexture.class.getSimpleName();


    static float vs[] = {
            1.0f, -1.0f, 1.0f, 0.0f,
            1.0f,  1.0f, 0.0f, 0.0f,
            -1.0f,  1.0f, 0.0f, 1.0f,
            -1.0f,  1.0f, 0.0f, 1.0f,
            -1.0f, -1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f, 0.0f,
    };

    int program;
    int vsShader;
    int fgShader;
    FloatBuffer vertexBuffer;

    public CameraTexture() {
        program = GLES20.glCreateProgram();
        vsShader = loadShader(GLES20.GL_VERTEX_SHADER, CameraShaders.Companion.getVsCode());
        fgShader = loadShader(GLES20.GL_FRAGMENT_SHADER, CameraShaders.Companion.getFsCode());

        GLES20.glAttachShader(program, vsShader);
        GLES20.glAttachShader(program, fgShader);

        GLES20.glLinkProgram(program);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(vs.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vs);
        vertexBuffer.position(0);

        GLES20.glBindAttribLocation(program, 0, "vPosition");
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetProgramiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public void draw(int texId) {

        GLES20.glUseProgram(program);

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 4 * 4, vertexBuffer);

        vertexBuffer.position(2);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 4 * 4, vertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId);
        GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        int sTextureLocation = GLES20.glGetUniformLocation(program, "sTexture");
        GLES20.glUniform1i(sTextureLocation, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisableVertexAttribArray(0);
        GLES20.glDisableVertexAttribArray(1);

    }
}
