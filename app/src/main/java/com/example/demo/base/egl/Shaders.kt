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

    }

}