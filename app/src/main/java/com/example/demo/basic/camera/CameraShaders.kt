package com.example.demo.basic.camera

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-10-07
 */
class CameraShaders {
    companion object{
        val vsCode="""
            #version 100
            precision mediump float; // 设置默认精度
            attribute vec4 vPosition;
            attribute vec2 vCoord;
            varying vec2 vTexCoord;
            
            void main() {
                gl_Position = vPosition;
                vTexCoord = vCoord;
            }
        """.trimIndent()
        val fsCode="""
            #version 100
            #extension GL_OES_EGL_image_external : require // 置于最前面
            precision mediump float; // 设置默认精度
            uniform samplerExternalOES sTexture;
            varying vec2 vTexCoord;
            
            void main() {
                gl_FragColor = texture2D(sTexture, vTexCoord);
            }

        """.trimIndent()
    }
}