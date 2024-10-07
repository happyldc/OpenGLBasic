package com.example.demo.base.egl

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-07
 */
class Shaders {
    companion object {
        val simple_vertex_shader = """
        attribute vec4 a_Position;
        void main() {
            gl_Position = a_Position;
        }

    """.trimIndent()

        val simple_fragment_shader = """
           precision mediump float;
           uniform vec4 u_Color;
           void main() {
              gl_FragColor = u_Color;
           }
      """.trimIndent()


        //纹理坐标
        val texture_vertex_shader = """
        attribute vec4 a_Position;//顶点坐标
        attribute vec2 f_Position;//纹理坐标 接受cpu传过来的顶点纹理坐标值
        varying vec2 ft_Position;// 用于vertex和fragment之间的传递的纹理坐标值
        
        void main() {
            ft_Position = f_Position;
            gl_Position = a_Position;
        }

    """.trimIndent()

        val texture_fragment_shader = """
           precision mediump float;
           varying vec2 ft_Position;
           uniform sampler2D sTexture;//uniform 用于在应用向vertex和fragment中传递值
           void main() {
              gl_FragColor = texture2D(sTexture,ft_Position);
           }
      """.trimIndent()


        val fbo1_texture_vertex_shader = """
            attribute vec4 vPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;

            void main() {
                gl_Position = vPosition;
                vTexCoord = aTexCoord;
            }
            
        """.trimIndent()
        val fbo1_texture_fragment_shader = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D sTexture;

            void main() {
                gl_FragColor = texture2D(sTexture, vTexCoord);
            }
        """.trimIndent()



        val matrix_texture_vertex_shader = """
            #version 100
            precision mediump float; // 设置默认精度
            attribute vec4 vPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            uniform mat4 uMatrix;

            void main() {
                gl_Position = uMatrix * vPosition;
                vTexCoord = aTexCoord;
            }
            
        """.trimIndent()
        val matrix_texture_fragment_shader = """
            #version 100
            #extension GL_OES_EGL_image_external : require // 置于最前面
            precision mediump float; // 设置默认精度
            varying vec2 vTexCoord;
            uniform sampler2D sTexture;

            void main() {
                gl_FragColor = texture2D(sTexture, vTexCoord);
            }
        """.trimIndent()

        val oes_texture_vertex_shader="""
            attribute vec4 position;
            attribute vec2 texCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = position;
                vTexCoord = texCoord;
            }
        """.trimIndent()
        val oes_texture_fragment_shader="""
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            varying vec2 vTexCoord;
            uniform samplerExternalOES oesTexture;
            void main() {
                gl_FragColor = texture2D(oesTexture, vTexCoord);
            }        
        """.trimIndent()

    }


}