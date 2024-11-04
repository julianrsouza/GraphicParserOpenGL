package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Visualizador3D {
    private long window;
    private Camera camera;
    private InputHandler inputHandler;

    public void run() {
        init();
        loop();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        // Inicialização do GLFW e criação da janela
        glfwInit();
        window = glfwCreateWindow(800, 600, "Visualizador 3D", NULL, NULL);
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        
        GL.createCapabilities();
        
        // Configuração da câmera e do handler de inputs
        camera = new Camera(new Vector3f(0.0f, 0.0f, 3.0f));
        inputHandler = new InputHandler(window);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Processa inputs para mover a câmera
            camera.processInput(inputHandler);

            // Obtém a matriz de visualização da câmera e aplica aos shaders
            Matrix4f viewMatrix = camera.getViewMatrix();
            // (Passe essa matriz para o shader aqui)

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Visualizador3D().run();
    }
}
