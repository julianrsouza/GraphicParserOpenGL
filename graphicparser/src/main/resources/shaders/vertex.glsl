#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
layout (location = 2) in vec2 texc;
layout (location = 3) in vec3 normal;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

// Variáveis que serão passadas para o fragment shader
out vec3 finalColor;
out vec2 texCoord;
out vec3 scaledNormal;
out vec3 fragPos;

void main()
{
    // Calcular a posição do vértice
    gl_Position = projection * view * model * vec4(position, 1.0);

    // Passa a cor para o fragment shader
    finalColor = color;

    // Passa a coordenada de textura (caso você tenha textura)
    texCoord = texc;

    // Passa a posição do fragmento para o fragment shader
    fragPos = vec3(model * vec4(position, 1.0));

    // Passa a normal escalada para o fragment shader
    scaledNormal = mat3(transpose(inverse(model))) * normal;
}
