package com.example.demo.basic.fbo4

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-15
 */
object Shaders {
    val oes_texture_vertex_shader="""
            attribute vec4 vPosition;
            attribute vec2 texCoord;
            varying vec2 vTexCoord;

            void main() {
                gl_Position = vPosition;
                vTexCoord = texCoord;
            }
            
        """.trimIndent()
    val oes_to_buffer_fragment_shader="""
            precision mediump float;
            varying vec2 vTexCoord;
            uniform samplerExternalOES oesTexture;
            void main() {
                gl_FragColor = texture2D(oesTexture, vTexCoord);
            }  
        """.trimIndent()
    val fbo_to_surface_fragment_shader="""
           precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D fboTexture;
            void main() {
                gl_FragColor = texture2D(fboTexture, vTexCoord);
            }
        """.trimIndent()

    val gray_fragment_shader="""
           precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D sTexture;
            void main() {
                 // 从纹理中采样颜色
                vec4 color = texture2D(sTexture, vTexCoord);
                // 计算灰度值
                float gray = 0.299 * color.r + 0.587 * color.g + 0.114 * color.b;
                // 输出灰度颜色，保持 alpha 值不变
                gl_FragColor = vec4(vec3(gray), color.a);
            }
          
            
          
        """.trimIndent()



}