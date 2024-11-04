package com.unisinos;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int programId;
    
    public Shader(String vertexPath, String fragmentPath) {
        String vertexCode;
        String fragmentCode;
        try {
            vertexCode = new String(Files.readAllBytes(Paths.get(vertexPath)), StandardCharsets.UTF_8);
            fragmentCode = new String(Files.readAllBytes(Paths.get(fragmentPath)), StandardCharsets.UTF_8);
            int vertexShader = compileShader(vertexCode, GL_VERTEX_SHADER);
            int fragmentShader = compileShader(fragmentCode, GL_FRAGMENT_SHADER);
            programId = glCreateProgram();
            glAttachShader(programId, vertexShader);
            glAttachShader(programId, fragmentShader);
            glLinkProgram(programId);
            
            if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
                throw new RuntimeException("Erro ao linkar programa: " + glGetProgramInfoLog(programId));
            }
            
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public void use() {
        glUseProgram(programId);
    }
    
    private int compileShader(String code, int type) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, code);
        glCompileShader(shaderId);
        
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Erro ao compilar shader: " + glGetShaderInfoLog(shaderId));
        }
        
        return shaderId;
    }
}
