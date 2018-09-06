#version 320 es

layout (location = 0) in vec4 position;
layout (location = 3) in vec3 in_color;
out vec3 out_color;

void main() {
    out_color = in_color;
    gl_PointSize = 20.0f;
    gl_Position = position;
}