package com.unisinos;

public class Material {
    private String name;       // Nome do material
    private float[] Ka;        // Componente de luz ambiente
    private float[] Kd;        // Componente de luz difusa
    private float[] Ks;        // Componente de luz especular
    private float Q;
    private int textureId;     // Identificador da textura (se existir)
    private String mapKd;      // Caminho para o mapa de textura difusa

    public Material(String name) {
        this.name = name;
        this.Ka = new float[] {1.0f, 1.0f, 1.0f}; // Valor padrão para ambiente
        this.Kd = new float[] {0.9f, 0.9f, 0.9f}; // Valor padrão para difusa
        this.Ks = new float[] {0.0f, 0.0f, 0.0f}; // Valor padrão para especular
        this.Q = 0;
        this.textureId = -1; // Sem textura por padrão
        this.mapKd = ""; // Caminho vazio por padrão
    }

    // Configurar cor ambiente
    public void setKa(float r, float g, float b) {
        this.Ka = new float[] {r, g, b};
    }

    // Configurar cor difusa
    public void setKd(float r, float g, float b) {
        this.Kd = new float[] {r, g, b};
    }

    // Configurar cor especular
    public void setKs(float r, float g, float b) {
        this.Ks = new float[] {r, g, b};
    }

    // Configurar e carregar textura
    public void setMapKd(String filePath) {
        this.mapKd = filePath;
        this.textureId = TextureLoader.loadTexture(filePath); // Carrega a textura
    }

    // Getters
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
