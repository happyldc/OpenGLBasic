#version 100
// 定义精度
precision mediump float;
// 统一变量，表示纹理
uniform sampler2D uTexture;
// 从顶点着色器传递过来的纹理坐标
varying vec2 vTexCoord;

void main() {
    // 根据纹理坐标获取对应的纹理颜色
    gl_FragColor = texture2D(uTexture, vTexCoord);
}
