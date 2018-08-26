#version 320 es

layout (location = 0) in vec4 position;

void main() {
    gl_PointSize = 20.0f;
    gl_Position = position;
}