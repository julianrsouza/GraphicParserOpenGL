package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.Arrays;
import java.util.List;

public class Main {
    // Variáveis para controle de objetos e câmera
    static Vector3f cameraPos = new Vector3f(0.0f, 0.0f, 5.0f);
    static Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
    static Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    static Vector3f cameraRight = new Vector3f(1.0f, 0.0f, 0.0f);
    static float angle = 0.0f;
    static float scaleFactor = 1.0f;
    static Vector3f objectPosition = new Vector3f(0.0f, 0.0f, 0.0f);
    static boolean rotateX = false, rotateY = false, rotateZ = false;
    static float pitch = 0.0f;
    static float yaw = 0.0f;

    public static void main(String[] args) {
        // Inicializa janela e shaders
        Janela janela = new Janela(800, 600, "Visualizador 3D");
        janela.init();

        Shader shader = new Shader("graphicparser\\src\\main\\resources\\shaders\\vertex.glsl", 
                                   "graphicparser\\src\\main\\resources\\shaders\\fragment.glsl");

        // Carrega os objetos .obj
        int[] nVertices1 = new int[1];
        int VAO1 = OBJLoader.loadSimpleOBJ("graphicparser\\src\\main\\resources\\obj\\suzanetriangle.obj", nVertices1);
        
        int[] nVertices2 = new int[1];
        int VAO2 = OBJLoader.loadSimpleOBJ("graphicparser\\src\\main\\resources\\obj\\Nave.obj", nVertices2);
        
        int[] nVertices3 = new int[1];
        int VAO3 = OBJLoader.loadSimpleOBJ("graphicparser\\src\\main\\resources\\obj\\cubotriangle.obj", nVertices3);
        
        // Verifica se houve erro ao carregar os modelos
        if (VAO1 == -1 || VAO2 == -1 || VAO3 == -1) {
            System.out.println("Falha ao carregar o modelo .obj");
            return;
        }

        // Cria os objetos 3D
        Objeto obj1 = new Objeto(VAO1, nVertices1[0], new Vector3f(-1.0f, 0.0f, 0.0f));
        Objeto obj2 = new Objeto(VAO2, nVertices2[0], new Vector3f(0.0f, 0.0f, 0.0f));
        Objeto obj3 = new Objeto(VAO3, nVertices3[0], new Vector3f(1.0f, 0.0f, 0.0f));

        List<Objeto> objetos = Arrays.asList(obj1, obj2, obj3);

        GLFW.glfwSetKeyCallback(janela.getWindowHandle(), (window, key, scancode, action, mods) -> {
            key_callback(janela.getWindowHandle(), key, scancode, action, mods, objetos);
        });

        // Matriz de visão (para mover a câmera)
        Matrix4f viewMatrix = new Matrix4f().lookAt(cameraPos, new Vector3f(0, 0, 0), cameraUp); // AQUI: Configura a matriz de visão inicial
        // Matriz de projeção (perspectiva)
        Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45), 800f / 600f, 0.1f, 100f);

        // Definir a posição e cor da luz
        Vector3f lightPos = new Vector3f(2.0f, 2.0f, 2.0f);
        Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);

        // Coeficientes de reflexão Phong
        float ka = 0.1f;
        float kd = 0.7f;
        float ks = 0.5f;
        float q = 32.0f;

        // Desabilitar culling para garantir que todas as faces dos objetos sejam renderizadas
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Loop principal da aplicação
        while (!GLFW.glfwWindowShouldClose(janela.getWindowHandle())) {
            GLFW.glfwPollEvents();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            shader.use();

            viewMatrix = new Matrix4f().lookAt(cameraPos, new Vector3f(0, 0, 0), cameraUp);

            // Atualiza matrizes de transformação e uniformes
            shader.setUniform("view", viewMatrix); // AQUI: viewMatrix recalculada
            shader.setUniform("projection", projectionMatrix);
            shader.setUniform("lightPos", lightPos);
            shader.setUniform("lightColor", lightColor);
            shader.setUniform("cameraPos", cameraPos); // AQUI: atualiza posição da câmera
            shader.setUniform("ka", ka);
            shader.setUniform("kd", kd);
            shader.setUniform("ks", ks);
            shader.setUniform("q", q);

            // Renderiza os objetos
            renderObjects(objetos, shader, angle, objectPosition, viewMatrix);

            // Limpar a ligação do VAO
            GL30.glBindVertexArray(0);

            GLFW.glfwSwapBuffers(janela.getWindowHandle());
        }

        shader.limpar();
        janela.limpar();
    }

    // Função de renderização dos objetos
    public static void renderObjects(List<Objeto> objetos, Shader shader, float angle, Vector3f objectPosition, Matrix4f viewMatrix) {
        Objeto objSelecionado = null;

        // Verifica qual objeto está selecionado
        for (Objeto obj : objetos) {
            if (obj.isSelected()) {
                objSelecionado = obj;
                break;
            }
        }

        if (objSelecionado != null) {
            // Resetar a modelMatrix para garantir que não haja acúmulo de transformações
            objSelecionado.modelMatrix = new Matrix4f();

            // Centraliza o objeto selecionado e aplica a escala de 80% da tela
            objSelecionado.modelMatrix.scale(0.8f);  // Aumenta a escala do objeto selecionado
            objSelecionado.modelMatrix.translate(objectPosition);  // Centraliza o objeto

            // Aplica rotações se necessário
            if (rotateX) {
                objSelecionado.modelMatrix.rotate(angle, new Vector3f(1.0f, 0.0f, 0.0f));
            } else if (rotateY) {
                objSelecionado.modelMatrix.rotate(angle, new Vector3f(0.0f, 1.0f, 0.0f));
            } else if (rotateZ) {
                objSelecionado.modelMatrix.rotate(angle, new Vector3f(0.0f, 0.0f, 1.0f));
            }

            // Atualiza a matriz do objeto no shader
            shader.setUniform("model", objSelecionado.modelMatrix);
            shader.setUniform("view", viewMatrix);
            shader.setUniform("cameraPos", cameraPos);

            // Renderiza o objeto selecionado
            GL30.glBindVertexArray(objSelecionado.VAO);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, objSelecionado.nVertices);
        } else {
            float totalWidth = 3 * 0.3f + 2.0f * 2.0f;
            float startX = -totalWidth / 2.0f + 0.3f / 2.0f;

            for (Objeto obj : objetos) {
                obj.modelMatrix = new Matrix4f();

                obj.modelMatrix.scale(0.3f);
                obj.modelMatrix.translate(new Vector3f(startX, 0.0f, 0.0f));

                shader.setUniform("model", obj.modelMatrix);
                shader.setUniform("view", viewMatrix);
                shader.setUniform("cameraPos", cameraPos);

                GL30.glBindVertexArray(obj.VAO);
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, obj.nVertices);

                startX += 0.3f + 2.0f;
            }

        }
    }

    
    // Função de controle de teclado
    public static void key_callback(long window, int key, int scancode, int action, int mods, List<Objeto> objects) {
        // Configurações de movimento
        float cameraSpeed = 0.1f; // Velocidade de movimento da câmera
        float rotationSpeed = 1.0f; // Velocidade de rotação da câmera
    
        if (key == GLFW.GLFW_KEY_1 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, 0);
        } else if (key == GLFW.GLFW_KEY_2 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, 1);
        } else if (key == GLFW.GLFW_KEY_3 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, 2);
        } else if (key == GLFW.GLFW_KEY_5 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, -1); // Restaura o estado inicial (sem seleção)
        }
    
        // Movimentos da câmera
        if (key == GLFW.GLFW_KEY_W && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            // Rotaciona a câmera para cima ao redor do eixo X (pitch)
            pitch += rotationSpeed;
            // Atualiza a direção da câmera com base no pitch (e no yaw, para rotação para os lados)
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_S && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            // Rotaciona a câmera para baixo ao redor do eixo X (pitch)
            pitch -= rotationSpeed;
            // Atualiza a direção da câmera com base no pitch (e no yaw, para rotação para os lados)
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_A && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            // Rotaciona a câmera para a esquerda ao redor do eixo Y (yaw)
            yaw += rotationSpeed;
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_D && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            // Rotaciona a câmera para a direita ao redor do eixo Y (yaw)
            yaw -= rotationSpeed;
            updateCameraDirection();
        }

        if (key == GLFW.GLFW_KEY_Q && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            cameraPos.mul(1.1f); // Aproxima a câmera (move para perto do objeto)
        } else if (key == GLFW.GLFW_KEY_E && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            cameraPos.mul(0.9f); // Afasta a câmera (move para longe do objeto)
        }
    
        // Movimentos laterais (Z e X)
        if (key == GLFW.GLFW_KEY_Z && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            cameraPos.add(cameraRight.mul(-cameraSpeed)); // Move para a esquerda
        } else if (key == GLFW.GLFW_KEY_X && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            cameraPos.add(cameraRight.mul(cameraSpeed)); // Move para a direita
        }
    }
    
    // Função para atualizar a direção da câmera com base no pitch e yaw
    private static void updateCameraDirection() {
        // Converte o pitch e o yaw para radianos
        float pitchRadians = (float)Math.toRadians(pitch);
        float yawRadians = (float)Math.toRadians(yaw);
    
        // Criando o quaternion de rotação
        Quaternionf pitchQuat = new Quaternionf().rotateAxis(pitchRadians, 1.0f, 0.0f, 0.0f);
        Quaternionf yawQuat = new Quaternionf().rotateAxis(yawRadians, 0.0f, 1.0f, 0.0f);
    
        // Multiplicando os quaternions para obter a rotação combinada
        Quaternionf rotation = yawQuat.mul(pitchQuat);
    
        // A direção da câmera deve ser no eixo Z positivo inicialmente
        Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);
        // Aplica a rotação ao vetor direção
        rotation.transform(direction);
    
        // Atualiza a posição da câmera com base na direção calculada
        cameraPos.set(direction).mul(5.0f);
    }
    


    
    public static void setSelectedObj(List<Objeto> objects, int index) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).setSelected(i == index); // Atualiza qual objeto está selecionado
        }
        if (index == -1) {
            cameraPos.set(0.0f, 0.0f, 5.0f); // Reseta a posição da câmera para o estado inicial
            objectPosition.set(0.0f, 0.0f, 0.0f); // Restaura posição padrão
        }
    }
}
