package com.unisinos;

import org.lwjgl.opengl.GL30;

public class Mesh {
    private int vao;
    private int vertexCount;

    public Mesh(int vao, int vertexCount) {
        this.vao = vao;
        this.vertexCount = vertexCount;
    }

    public int getVao() {
        return vao;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanup() {
        GL30.glDeleteVertexArrays(vao);
    }
}
