#version 430

in vec3 finalColor;    // Cor atribuída pelos vértices ou pelo material
in vec2 texCoord;      // Coordenadas da textura (passadas pelo vertex shader)
in vec3 scaledNormal;  // Normal ajustada para escala
in vec3 fragPos;       // Posição do fragmento no espaço do mundo

// Propriedades da superfície
uniform float ka, kd, ks, q;

// Propriedades da fonte de luz
uniform vec3 lightPos, lightColor;

// Propriedades da câmera
uniform vec3 cameraPos;

// Textura
uniform sampler2D textureSampler; // A textura será ligada ao ID

out vec4 color;

void main()
{
    // Coeficiente de luz ambiente
    vec3 ambient = ka * lightColor;

    // Coeficiente de reflexão difusa
    vec3 N = normalize(scaledNormal);
    vec3 L = normalize(lightPos - fragPos);
    float diff = max(dot(N, L), 0.0);
    vec3 diffuse = kd * diff * lightColor;

    // Coeficiente de reflexão especular
    vec3 R = normalize(reflect(-L, N));
    vec3 V = normalize(cameraPos - fragPos);
    float spec = max(dot(R, V), 0.0);
    spec = pow(spec, q);
    vec3 specular = ks * spec * lightColor;

    // Recupera a cor da textura
    vec4 texColor = texture(textureSampler, texCoord);

    // Calcula a cor final
    vec3 result = (ambient + diffuse) * texColor.rgb + specular;

    // Define a cor final
    color = vec4(result, texColor.a); // Use o canal alfa da textura
}
