#version 410 core

in vec2 TexCoord;
in vec3 Normal;
in vec3 FragPos;
in vec3 AmbientA;
in vec3 DiffuseA;
in vec3 SpecularA;
in float ShininessA;

out vec4 color;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColor;
uniform vec3 cameraPos;

uniform sampler2D texture1;

void main() {
    vec4 objectColor = texture(texture1, TexCoord);

    // ambient
    vec3 ambient = lightColor * AmbientA;
      
    // diffuse
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor * DiffuseA;
    
    // specular
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), ShininessA);
    vec3 specular = spec * lightColor * SpecularA;
        
    vec3 result = ambient + diffuse + specular;
    color = vec4(result, 1.0) * objectColor;

    // Calculate fog
    vec4  fog_colour = vec4(0.3, 0.3, 0.3, 1.0);
    float dist = distance(cameraPos, FragPos);
    float fatm = 1/dist;
    
    color = color * fatm + (1 - fatm) * fog_colour;
}
