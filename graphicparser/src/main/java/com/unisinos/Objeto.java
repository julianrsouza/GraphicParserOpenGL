package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Vector3f;

class Objeto {
    int VAO;
    int nVertices;
    Vector3f position;
    Matrix4f modelMatrix;
    boolean selected = false;
    
    public Objeto(int VAO, int nVertices, Vector3f position) {
        this.VAO = VAO;
        this.nVertices = nVertices;
        this.position = position;
        this.modelMatrix = new Matrix4f();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void updateModelMatrix(float scale) {
        this.modelMatrix = new Matrix4f().translate(position).scale(scale);
    }
}

