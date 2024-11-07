package com;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class Shader {

    private int programID;

    public Shader(String vertexPath, String fragmentPath) throws IOException {
        String vertexSource = new String(Files.readAllBytes(Paths.get(vertexPath)));
        String fragmentSource = new String(Files.readAllBytes(Paths.get(fragmentPath)));

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexSource);
        GL20.glCompileShader(vertexShader);
        if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            throw new RuntimeException("Erro ao compilar o shader: " + GL20.glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentSource);
        GL20.glCompileShader(fragmentShader);
        if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            throw new RuntimeException("Erro ao compilar o shader: " + GL20.glGetShaderInfoLog(fragmentShader));
        }

        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShader);
        GL20.glAttachShader(programID, fragmentShader);
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    public void use() {
        GL20.glUseProgram(programID);
    }

    public void setUniform(String name, Matrix4f matrix) {
        int location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) {
            System.err.println("Uniforme não encontrado: " + name);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniform(String name, Vector3f vector) {
        int location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) {
            System.err.println("Uniforme não encontrado: " + name);
        }
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    public void setUniform(String name, float value) {
        int location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) {
            System.err.println("Uniforme não encontrado: " + name);
        }
        GL20.glUniform1f(location, value);
    }

    public void setUniform(String name, int value) {
        int location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) {
            System.err.println("Uniforme não encontrado: " + name);
        }
        GL20.glUniform1i(location, value);
    }

    public int getProgramID() {
        return programID;
    }

    public void cleanup() {
        GL20.glDeleteProgram(programID);
    }
}
