package com.unisinos;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.GLFWVidMode;

public class Janela {
    private long window;
    private int largura;
    private int altura;
    private String titulo;

    public Janela(int largura, int altura, String titulo) {
        this.largura = largura;
        this.altura = altura;
        this.titulo = titulo;
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Não foi possível inicializar o GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        
        window = GLFW.glfwCreateWindow(largura, altura, titulo, 0, 0);
        if (window == 0) {
            throw new RuntimeException("Erro ao criar a janela GLFW");
        }

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (videoMode != null) {
            int monitorLargura = videoMode.width();
            int monitorAltura = videoMode.height();
            GLFW.glfwSetWindowPos(window, 
                (monitorLargura - largura) / 2, 
                (monitorAltura - altura) / 2);
        }
        
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        GLFW.glfwSwapInterval(1);

        GL11.glViewport(0, 0, largura, altura);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void limpar() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public long getWindowHandle() {
        return window;
    }
}
