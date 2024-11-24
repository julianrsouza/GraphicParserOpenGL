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

    public Objeto(Vector3f position) {
        this.meshes = new ArrayList<>();
        this.position = position;
        this.modelMatrix = new Matrix4f();
        this.selected = false;
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

    public void updateModelMatrix(float scale) {
        this.modelMatrix = new Matrix4f().translate(position).scale(scale);
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}
