package com.unisinos;

public class Material {
    private String name;
    private float[] Ka;
    private float[] Kd;
    private float[] Ks;
    private float Q;
    private int textureId;
    private String mapKd;

    public Material(String name) {
        this.name = name;
        this.Ka = new float[] {1.0f, 1.0f, 1.0f};
        this.Kd = new float[] {0.9f, 0.9f, 0.9f};
        this.Ks = new float[] {0.0f, 0.0f, 0.0f};
        this.Q = 0;
        this.textureId = -1;
        this.mapKd = "";
    }

    public void setKa(float r, float g, float b) {
        this.Ka = new float[] {r, g, b};
    }

    public void setKd(float r, float g, float b) {
        this.Kd = new float[] {r, g, b};
    }

    public void setKs(float r, float g, float b) {
        this.Ks = new float[] {r, g, b};
    }

    public void setMapKd(String filePath) {
        this.mapKd = filePath;
        this.textureId = TextureLoader.loadTexture(filePath);
    }

    public String getName() {
        return name;
    }

    public float[] getKa() {
        return Ka;
    }

    public float[] getKd() {
        return Kd;
    }

    public float[] getKs() {
        return Ks;
    }
    
    public float getQ() {
        return Q;
    }

    public void setQ(float q) {
        Q = q;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public String getMapKd() {
        return mapKd;
    }
}
