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
    static float angleX = 0.0f;
    static float angleY = 0.0f;
    static float angleZ = 0.0f;
    static float scaleFactor = 1.0f;
    static Vector3f objectPosition = new Vector3f(0.0f, 0.0f, 0.0f);
    static boolean rotateXmore = false, rotateYmore = false, rotateZmore = false;
    static boolean rotateXminus = false, rotateYminus = false, rotateZminus = false;
    static float pitch = 0.0f;
    static float yaw = 0.0f;
    static float distance = 5.0f;
    static float rotationSpeed = 1.0f;
    static Objeto objSelecionado = null;
    static float scaleStep = 0.1f;

    public static void main(String[] args) {
        // Inicializa janela e shaders
        Janela janela = new Janela(800, 600, "Visualizador 3D");
        janela.init();

        Shader shader = new Shader("graphicparser\\src\\main\\resources\\shaders\\vertex.glsl", 
                                   "graphicparser\\src\\main\\resources\\shaders\\fragment.glsl");

        List<Mesh> meshes1 = OBJLoader.loadOBJWithMultipleMeshes("graphicparser\\src\\main\\resources\\obj\\Pikachu.obj");
        //List<Mesh> meshes2 = OBJLoader.loadOBJWithMultipleMeshes("graphicparser\\src\\main\\resources\\obj\\suzanetriangle.obj");
        //List<Mesh> meshes3 = OBJLoader.loadOBJWithMultipleMeshes("graphicparser\\src\\main\\resources\\obj\\nave.obj");

        Objeto obj1 = createObject(new Vector3f(-1.0f, 0.0f, 0.0f), meshes1);
        //Objeto obj2 = createObject(new Vector3f(0.0f, 0.0f, 0.0f), meshes2);
        //Objeto obj3 = createObject(new Vector3f(1.0f, 0.0f, 0.0f), meshes3);
        List<Objeto> objetos = Arrays.asList(obj1);

        GLFW.glfwSetKeyCallback(janela.getWindowHandle(), (window, key, scancode, action, mods) -> {
            key_callback(janela.getWindowHandle(), key, scancode, action, mods, objetos);
        });

        // Matriz de visão (para mover a câmera)
        Matrix4f viewMatrix = new Matrix4f().lookAt(cameraPos, cameraPos.add(cameraFront), cameraUp);
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

            objSelecionado = getSelectedObject(objetos);

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            shader.use();

            if (rotateXmore) {
                angleX += 0.02;
            }

            if (rotateXminus) {
                angleX -= 0.02;
            }

            if (rotateYmore) {
                angleY += 0.02;
            }

            if (rotateYminus) {
                angleY -= 0.02;
            }
            
            if (rotateZmore) {
                angleZ += 0.02;
            }

            if (rotateZminus) {
                angleZ -= 0.02;
            }

            viewMatrix = new Matrix4f().lookAt(cameraPos, new Vector3f(0, 0, 0), cameraUp);

            // Atualiza matrizes de transformação e uniformes
            shader.setUniform("view", viewMatrix);
            shader.setUniform("projection", projectionMatrix);
            shader.setUniform("lightPos", lightPos);
            shader.setUniform("lightColor", lightColor);
            shader.setUniform("cameraPos", cameraPos);
            shader.setUniform("ka", ka);
            shader.setUniform("kd", kd);
            shader.setUniform("ks", ks);
            shader.setUniform("q", q);

            // Renderiza os objetos
            renderObjects(objetos, shader, angleX, angleY, angleZ, viewMatrix);

            // Limpar a ligação do VAO
            GL30.glBindVertexArray(0);

            GLFW.glfwSwapBuffers(janela.getWindowHandle());
        }

        shader.limpar();
        janela.limpar();
    }

    // Função de renderização dos objetos
    // Função de renderização dos objetos
    public static void renderObjects(List<Objeto> objetos, Shader shader, float angleX, float angleY, float angleZ, Matrix4f viewMatrix) {
        if (objSelecionado != null) {
            // Resetar a modelMatrix para garantir que não haja acúmulo de transformações
            objSelecionado.modelMatrix = new Matrix4f();
    
            // Usar o valor de escala do objeto selecionado
            objSelecionado.modelMatrix.scale(objSelecionado.getScale());  // Aplica a escala real
    
            // Aplica a translação do objeto
            objSelecionado.modelMatrix.translate(objSelecionado.getPosition());  // Usa a posição do objeto selecionado
    
            // Aplica rotações se necessário
            objSelecionado.modelMatrix
            .rotateX(angleX)  // Aplica a rotação acumulada no eixo X
            .rotateY(angleY)  // Aplica a rotação acumulada no eixo Y
            .rotateZ(angleZ);
    
            // Atualiza a matriz do objeto no shader
            shader.setUniform("model", objSelecionado.modelMatrix);
            shader.setUniform("view", viewMatrix);
            shader.setUniform("cameraPos", cameraPos);
    
            // Renderiza as malhas do objeto selecionado
            for (Mesh mesh : objSelecionado.getMeshes()) {
                if (mesh.getMaterial().getTextureId() != -1) {
                    GL30.glBindTexture(GL30.GL_TEXTURE_2D, mesh.getMaterial().getTextureId());
                }
                GL30.glBindVertexArray(mesh.getVao());
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
            }
    
            GL30.glBindVertexArray(0);
        } else {
            // Caso nenhum objeto esteja selecionado, renderiza todos os objetos de forma padrão
            float totalWidth = 3 * 0.3f + 2.0f * 2.0f;
            float startX = -totalWidth / 2.0f + 0.3f / 2.0f;
    
            for (Objeto obj : objetos) {
                obj.modelMatrix = new Matrix4f();
    
                // Usar uma escala padrão para objetos não selecionados
                obj.modelMatrix.scale(0.3f);  // Escala padrão para objetos não selecionados
                obj.modelMatrix.translate(new Vector3f(startX, 0.0f, 0.0f));
    
                // Aplica rotações se necessário
                obj.modelMatrix
                .rotateX(angleX)  // Aplica a rotação acumulada no eixo X
                .rotateY(angleY)  // Aplica a rotação acumulada no eixo Y
                .rotateZ(angleZ);
    
                // Atualiza a matriz do objeto no shader
                shader.setUniform("model", obj.modelMatrix);
                shader.setUniform("view", viewMatrix);
                shader.setUniform("cameraPos", cameraPos);
    
                // Renderiza as malhas de todos os objetos
                for (Mesh mesh : obj.getMeshes()) {
                    if (mesh.getMaterial().getTextureId() != -1) {
                        GL30.glBindTexture(GL30.GL_TEXTURE_2D, mesh.getMaterial().getTextureId());
                    }
                    GL30.glBindVertexArray(mesh.getVao());
                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
                }
    
                GL30.glBindVertexArray(0);
    
                startX += 0.3f + 2.0f;
            }
        }
    }
    
    // Função de controle de teclado
    public static void key_callback(long window, int key, int scancode, int action, int mods, List<Objeto> objects) {
        // Configurações de movimento
        float cameraSpeed = 0.1f; // Velocidade de movimento da câmera
    
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
    
        // Controle de zoom com Q e E
        if (key == GLFW.GLFW_KEY_Q && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            distance *= 1.1f; // Aproxima a câmera
            updateCameraDirection(); // Recalcula a posição da câmera com a nova distância
        } else if (key == GLFW.GLFW_KEY_E && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            distance *= 0.9f; // Afasta a câmera
            updateCameraDirection(); // Recalcula a posição da câmera com a nova distância
        }
        
        if (objSelecionado != null) {
            if (key == GLFW.GLFW_KEY_UP && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().y += 0.1f; // Move o objeto para cima
                objSelecionado.updateModelMatrix(); // Atualiza a matriz do objeto
            } else if (key == GLFW.GLFW_KEY_DOWN && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().y -= 0.1f; // Move o objeto para baixo
                objSelecionado.updateModelMatrix(); // Atualiza a matriz do objeto
            } else if (key == GLFW.GLFW_KEY_LEFT && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().x -= 0.1f; // Move o objeto para a esquerda
                objSelecionado.updateModelMatrix(); // Atualiza a matriz do objeto
            } else if (key == GLFW.GLFW_KEY_RIGHT && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().x += 0.1f; // Move o objeto para a direita
                objSelecionado.updateModelMatrix(); // Atualiza a matriz do objeto
            }
            if (objSelecionado != null) {
                if (key == GLFW.GLFW_KEY_KP_ADD && action == GLFW.GLFW_PRESS) {
                    objSelecionado.setScale(objSelecionado.getScale() + scaleStep);
                    System.out.println(objSelecionado.getScale());
                    objSelecionado.updateModelMatrix();
                } else if (key == GLFW.GLFW_KEY_KP_SUBTRACT && action == GLFW.GLFW_PRESS) {
                    float newScale = Math.max(0.1f, objSelecionado.getScale() - scaleStep);
                    objSelecionado.setScale(newScale);
                    System.out.println(objSelecionado.getScale());
                    objSelecionado.updateModelMatrix();
                }
            }
            if (key == GLFW.GLFW_KEY_KP_2 && action == GLFW.GLFW_PRESS) {
                rotateXminus = !rotateXminus;
            } else if (key == GLFW.GLFW_KEY_KP_8 && action == GLFW.GLFW_PRESS) {
                rotateXmore = !rotateXmore;
            } else if (key == GLFW.GLFW_KEY_KP_4 && action == GLFW.GLFW_PRESS) {
                rotateYminus = !rotateYminus;
            } else if (key == GLFW.GLFW_KEY_KP_6 && action == GLFW.GLFW_PRESS) {
                rotateYmore = !rotateYmore;
            } else if (key == GLFW.GLFW_KEY_KP_1 && action == GLFW.GLFW_PRESS) {
                rotateZminus = !rotateZminus;
            } else if (key == GLFW.GLFW_KEY_KP_3 && action == GLFW.GLFW_PRESS) {
                rotateZmore = !rotateZmore;
            }
        }
    }
    
    // Função para atualizar a direção da câmera com base no pitch e yaw
    private static void updateCameraDirection() {
        // Converte o pitch e o yaw para radianos
        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);
    
        // Criando os quaternions para as rotações de pitch e yaw
        Quaternionf pitchQuat = new Quaternionf().rotateAxis(pitchRadians, 1.0f, 0.0f, 0.0f);
        Quaternionf yawQuat = new Quaternionf().rotateAxis(yawRadians, 0.0f, 1.0f, 0.0f);
    
        // Multiplicando os quaternions para obter a rotação combinada
        Quaternionf rotation = yawQuat.mul(pitchQuat);
    
        // A direção inicial da câmera é no eixo Z positivo
        Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);
    
        // Aplica a rotação ao vetor direção
        rotation.transform(direction);
    
        // Atualiza a posição da câmera com a direção calculada, mantendo a distância
        cameraPos.set(direction).mul(distance);
    }

    private static Objeto getSelectedObject(List<Objeto> objetos) {
        Objeto objSelecionado = null;
        for (Objeto obj : objetos) {
            if (obj.isSelected()) {
                objSelecionado = obj;
                break;
            }
        }
        return objSelecionado;
    }

    public static void setSelectedObj(List<Objeto> objects, int index) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).setSelected(i == index);
        }
        if (index == -1) {
            cameraPos.set(0.0f, 0.0f, 5.0f);
            objectPosition.set(0.0f, 0.0f, 0.0f);
        }
    }

    private static Objeto createObject( Vector3f vector, List<Mesh> meshes) {
        Objeto objeto = new Objeto(vector);
        for (Mesh mesh : meshes) {
            objeto.addMesh(mesh);
        }
        return objeto;
    }
}
