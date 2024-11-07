#version 330 core

layout(location = 0) in vec3 aPos; // Posição do vértice como atributo de entrada

uniform mat4 model;   // Matriz do modelo para aplicar transformações ao objeto
uniform mat4 view;    // Matriz de visão para o controle de câmera
uniform mat4 projection; // Matriz de projeção para perspectiva

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}