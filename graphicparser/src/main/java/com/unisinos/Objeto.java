package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class Objeto {
    private List<Mesh> meshes;
    private Vector3f position;
    public Matrix4f modelMatrix;
    private boolean selected;
    private float scale;

    public Objeto(Vector3f position) {
        this.meshes = new ArrayList<>();
        this.position = position;
        this.modelMatrix = new Matrix4f();
        this.selected = false;
        this.scale = 1.0f;
        updateModelMatrix();
    }

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void updateModelMatrix() {
        this.modelMatrix = new Matrix4f()
            .scale(this.scale)
            .translate(this.position);
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        updateModelMatrix();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        updateModelMatrix();
    }
}
