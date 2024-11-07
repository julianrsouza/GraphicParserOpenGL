package com;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Main {
    
    private long window;
    private Shader shader;
    private int vao;
    private int vbo;
    private int ebo;

    public void run() {
        System.out.println("LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Falha ao inicializar o GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(800, 600, "Janela OpenGL", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Falha ao criar a janela GLFW");
        }

        GLFW.glfwSetWindowPos(window, 100, 100);

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        try {
            // Corrigido para caminho relativo ao src/main/resources
            shader = new Shader(
                "C:\\Users\\buian\\OneDrive\\Documentos\\GitHub\\GraphicParserOpenGL\\graphicparser\\src\\main\\resources\\shaders\\vertex.glsl", 
                "C:\\Users\\buian\\OneDrive\\Documentos\\GitHub\\GraphicParserOpenGL\\graphicparser\\src\\main\\resources\\shaders\\fragment.glsl"
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar os shaders");
        }

         float[] vertices = {
            // Posições          // Cores (apenas para teste)
            -0.5f,  0.5f, 0.0f,  1.0f, 0.0f, 0.0f,  // Canto superior esquerdo (vermelho)
            0.5f,  0.5f, 0.0f,  0.0f, 1.0f, 0.0f,  // Canto superior direito (verde)
            0.5f, -0.5f, 0.0f,  0.0f, 0.0f, 1.0f,  // Canto inferior direito (azul)
            -0.5f, -0.5f, 0.0f,  1.0f, 1.0f, 0.0f   // Canto inferior esquerdo (amarelo)
        };

        // Índices para formar o quadrado
        int[] indices = {
            0, 1, 2,  // Triângulo superior
            2, 3, 0   // Triângulo inferior
        };

        // Criando os buffers
        vao = GL30.glGenVertexArrays();
        vbo = GL20.glGenBuffers();
        ebo = GL20.glGenBuffers();

        // Configurar o VAO
        GL30.glBindVertexArray(vao);

        // VBO (dados dos vértices)
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vbo);
        GL20.glBufferData(GL20.GL_ARRAY_BUFFER, vertices, GL20.GL_STATIC_DRAW);

        // EBO (índices)
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indices, GL20.GL_STATIC_DRAW);

        // Posicionamento dos atributos de vértices
        GL20.glVertexAttribPointer(0, 3, GL20.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Atributos de cor
        GL20.glVertexAttribPointer(1, 3, GL20.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);  // Desvincula o VAO

        // Matriz de projeção (perspectiva)
        Matrix4f projection = new Matrix4f();
        projection.perspective((float) Math.toRadians(45.0), 800f / 600f, 0.1f, 1000f);

        // Matriz de visualização (câmera)
        Matrix4f view = new Matrix4f();
        view.lookAt(new Vector3f(0, 0, -3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

        // Matriz do modelo (transformação do objeto)
        Matrix4f model = new Matrix4f();
        model.identity();

        // Usando o shader e passando as matrizes para ele
        shader.use();
        shader.setUniform("projection", projection);
        shader.setUniform("view", view);
        shader.setUniform("model", model);
    }

    private void loop() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Ativando o shader
            shader.use();

            // Renderizando o quadrado
            GL30.glBindVertexArray(vao);  // Vincula o VAO
            GL30.glDrawElements(GL30.GL_TRIANGLES, 6, GL30.GL_UNSIGNED_INT, 0);  // Desenha os triângulos

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
