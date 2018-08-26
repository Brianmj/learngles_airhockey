#version 320 es

precision mediump float;

layout(location = 8) uniform vec4 u_color;
out vec4 color;

void main() {
    color = u_color;
}