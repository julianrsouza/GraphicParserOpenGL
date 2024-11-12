package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Main {
    public static void main(String[] args) {
        Janela janela = new Janela(800, 600, "Visualizador 3D");
        janela.init();

        Shader shader = new Shader("graphicparser\\src\\main\\resources\\shaders\\vertex.glsl", 
                                   "graphicparser\\src\\main\\resources\\shaders\\fragment.glsl");

        int[] nVertices1 = new int[1];
        int VAO = OBJLoader.loadSimpleOBJ("graphicparser\\src\\main\\resources\\obj\\suzanetriangle.obj", nVertices1);
        
        int[] nVertices2 = new int[1];
        int VAO2 = OBJLoader.loadSimpleOBJ("graphicparser\\src\\main\\resources\\obj\\Nave.obj", nVertices2);
        
        int[] nVertices3 = new int[1];
        int VAO3 = OBJLoader.loadSimpleOBJ("graphicparser\\src\\main\\resources\\obj\\cubotriangle.obj", nVertices3);
        if (VAO == -1) {
            System.out.println("Falha ao carregar o modelo .obj");
            return;
        }

        // Ajuste da posição da câmera
        Matrix4f viewMatrix = new Matrix4f().translate(0, 0, -3);  // Mantendo o valor original de -3 para a câmera
        // Ajuste na projeção
        Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45), 800f / 600f, 0.1f, 100f);
    
        Vector3f lightPos = new Vector3f(2.0f, 2.0f, 2.0f);
        Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f cameraPos = new Vector3f(0.0f, 0.0f, 5.0f);
        
        // Coeficientes de reflexão Phong
        float ka = 0.1f;
        float kd = 0.7f;
        float ks = 0.5f;
        float q = 32.0f;
        
        // Desabilitar culling para garantir que todas as faces do cubo sejam renderizadas
        GL11.glDisable(GL11.GL_CULL_FACE);
        
        while (!GLFW.glfwWindowShouldClose(janela.getWindowHandle())) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
            shader.use();
        
            // Definir matrizes de transformação
            shader.setUniform("view", viewMatrix);
            shader.setUniform("projection", projectionMatrix);
        
            // Definir uniformes de iluminação
            shader.setUniform("lightPos", lightPos);
            shader.setUniform("lightColor", lightColor);
            shader.setUniform("cameraPos", cameraPos);
        
            // Definir coeficientes de reflexão
            shader.setUniform("ka", ka);
            shader.setUniform("kd", kd);
            shader.setUniform("ks", ks);
            shader.setUniform("q", q);

            // 1º Objeto - Suzane
            Matrix4f modelMatrix1 = new Matrix4f().translate(-1.0f, 0.0f, 0.0f).scale(0.3f);
            shader.setUniform("model", modelMatrix1);
            GL30.glBindVertexArray(VAO);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, nVertices1[0]);

            // 2º Objeto - Nave
            Matrix4f modelMatrix2 = new Matrix4f().translate(0.0f, 0.0f, 0.0f).scale(0.3f);
            shader.setUniform("model", modelMatrix2);
            GL30.glBindVertexArray(VAO2);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, nVertices2[0]);

            // 3º Objeto - Cubo
            Matrix4f modelMatrix3 = new Matrix4f().translate(1.0f, 0.0f, 0.0f).scale(0.3f);
            shader.setUniform("model", modelMatrix3);
            GL30.glBindVertexArray(VAO3);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, nVertices3[0]);

            // Limpar a ligação do VAO
            GL30.glBindVertexArray(0);
        
            GLFW.glfwSwapBuffers(janela.getWindowHandle());
            GLFW.glfwPollEvents();
        }

        shader.limpar();
        janela.limpar();
    }
}
