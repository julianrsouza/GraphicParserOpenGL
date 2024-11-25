package com.unisinos;

import org.lwjgl.opengl.GL30;

public class Mesh {
    private int vao;
    private int vertexCount;
    private Material material;

    public Mesh(int vao, int vertexCount, Material material) {
        this.vao = vao;
        this.vertexCount = vertexCount;
        this.material = material;
    }

    public int getVao() {
        return vao;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    public void cleanup() {
        GL30.glDeleteVertexArrays(vao);
    }
}

