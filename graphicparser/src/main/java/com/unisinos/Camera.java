package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private float yaw;
    private float pitch;
    private float speed = 0.05f;
    private float sensitivity = 0.1f;

    public Camera(Vector3f position) {
        this.position = position;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.yaw = -90.0f;
        this.pitch = 0.0f;
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraTarget = new Vector3f();
        position.add(front, cameraTarget);
        return new Matrix4f().lookAt(position, cameraTarget, up);
    }

    public void processInput(InputHandler inputHandler) {
        // Controle de posição
        if (inputHandler.isKeyPressed(GLFW_KEY_W))
            position.add(new Vector3f(front).mul(speed));
        if (inputHandler.isKeyPressed(GLFW_KEY_S))
            position.sub(new Vector3f(front).mul(speed));
        if (inputHandler.isKeyPressed(GLFW_KEY_A))
            position.sub(new Vector3f(front).cross(up).normalize().mul(speed));
        if (inputHandler.isKeyPressed(GLFW_KEY_D))
            position.add(new Vector3f(front).cross(up).normalize().mul(speed));

        // Controle de rotação
        float offsetX = (float) inputHandler.getDeltaX() * sensitivity;
        float offsetY = (float) inputHandler.getDeltaY() * sensitivity;
        
        yaw += offsetX;
        pitch -= offsetY;
        
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        // Calcula a direção do vetor da câmera
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.normalize();
    }
    
    public Vector3f getPosition() {
        return position;
    }
}
