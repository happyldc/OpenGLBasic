#version 100
attribute vec4 aPosition;
attribute vec2 aTexCoord;
varying vec2 vTexCoord;
uniform mat4 uRotationMatrix;  // 添加旋转矩阵的统一变量

void main() {
    // 应用旋转矩阵到顶点坐标
    gl_Position = uRotationMatrix * aPosition;
    vTexCoord = aTexCoord;
}