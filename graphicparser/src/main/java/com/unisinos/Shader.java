package com.unisinos;

import org.lwjgl.opengl.GL20;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Shader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public Shader(String vertexPath, String fragmentPath) {
        // Cria o programa shader
        programId = GL20.glCreateProgram();
        
        // Compila e anexa os shaders
        vertexShaderId = loadShader(vertexPath, GL20.GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);
        
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        
        GL20.glLinkProgram(programId);
        
        // Verifica se a vinculação foi bem-sucedida
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            throw new RuntimeException("Erro ao linkar o programa shader: " + GL20.glGetProgramInfoLog(programId));
        }
        
        // Os shaders individuais podem ser excluídos após a linkagem
        GL20.glDetachShader(programId, vertexShaderId);
        GL20.glDetachShader(programId, fragmentShaderId);
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
    }

    private int loadShader(String path, int type) {
        // Carrega o código do shader a partir do arquivo
        StringBuilder source = new StringBuilder();
        try {
            Files.lines(Paths.get(path)).forEach(line -> source.append(line).append("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler o arquivo do shader: " + path, e);
        }
        
        // Cria e compila o shader
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);

        // Verifica se a compilação foi bem-sucedida
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            throw new RuntimeException("Erro ao compilar o shader: " + path + "\n" + GL20.glGetShaderInfoLog(shaderId));
        }

        return shaderId;
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void setUniform(String name, Matrix4f value) {
        // Define uma matriz 4x4 como variável uniforme
        int location = GL20.glGetUniformLocation(programId, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        GL20.glUniformMatrix4fv(location, false, buffer);
    }

    public void setUniform(String name, float value) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform1f(location, value);
    }

    public void setUniform(String name, int value) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform1i(location, value);
    }

    public void setUniform(String name, Vector3f value) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform3f(location, value.x, value.y, value.z);
    }

    public void limpar() {
        // Exclui o programa shader ao final
        GL20.glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }
}
