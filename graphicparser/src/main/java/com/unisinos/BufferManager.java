package com.unisinos;
import static org.lwjgl.opengl.GL30.*;

public class BufferManager {
    private int vaoId, vboId;
    
    public BufferManager(float[] vertices) {
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        
        glBindVertexArray(vaoId);
        
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        // Configuração dos atributos de vértice
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0); // Vértices
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1); // Normais

        glBindVertexArray(0);
    }
    
    public void render() {
        glBindVertexArray(vaoId);
        glDrawArrays(GL_TRIANGLES, 0, 36); // Desenho de triângulos
        glBindVertexArray(0);
    }
}

