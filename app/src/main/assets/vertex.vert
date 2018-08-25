#version 320 es

layout(location = 0) in vec4 positon;

void main()
{
    gl_Position = positon;
}