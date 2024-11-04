package com.unisinos;

import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL30;

public class Modelo3D {
    private AIScene scene;
    
    public Modelo3D(String filePath) {
        scene = Assimp.aiImportFile(filePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenNormals);
        if (scene == null) {
            throw new RuntimeException("Erro ao carregar modelo: " + Assimp.aiGetErrorString());
        }
    }
   
    private void setupModel() {
        BufferManager bufferManager = new BufferManager();
        this.vaoID = bufferManager.createVAO();
        this.vboID = bufferManager.createVBO(verticesData);
        this.eboID = bufferManager.createEBO(indicesData);
    }

    public void render() {
        // Usar VAO para desenhar o modelo
        GL30.glBindVertexArray(vaoID);
        // Aqui você deve usar o glDrawElements ou glDrawArrays para renderizar
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        // Liberar os recursos do OpenGL quando não forem mais necessários
        GL30.glDeleteVertexArrays(vaoID);
        GL30.glDeleteBuffers(vboID);
        GL30.glDeleteBuffers(eboID);
    }
}
