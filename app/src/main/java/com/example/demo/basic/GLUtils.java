package com.example.demo.basic;

import android.opengl.GLES20;
import android.util.Log;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-10-07
 */
public class GLUtils {
    private static final String TAG = "GLUtils";

    public static int loadShader(int type, String shaderCode) {
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
}
