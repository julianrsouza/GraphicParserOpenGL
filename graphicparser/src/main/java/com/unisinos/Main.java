package com.unisinos;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {
    // Variáveis para controle de objetos e câmera
    static Vector3f cameraPos;
    static Vector3f cameraFront;
    static Vector3f cameraUp;
    static Vector3f cameraRight;
    static float angleX = 0.0f;
    static float angleY = 0.0f;
    static float angleZ = 0.0f;
    static float scaleFactor = 1.0f;
    static Vector3f objectPosition = new Vector3f(0.0f, 0.0f, 0.0f);
    static boolean rotateXmore = false, rotateYmore = false, rotateZmore = false;
    static boolean rotateXminus = false, rotateYminus = false, rotateZminus = false;
    static float pitch = 0.0f;
    static float yaw = 0.0f;
    static float distance = 12.0f;
    static float rotationSpeed = 1.0f;
    static Objeto objSelecionado = null;
    static float scaleStep = 0.1f;
    static boolean parametricCurveOn;

    public static void main(String[] args) {
        // Inicializa janela e shaders
        Janela janela = new Janela(800, 600, "Visualizador 3D");
        janela.init();

        GL11.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);

        Shader shader = new Shader(Paths.get("graphicparser", "src", "main", "resources", "shaders", "vertex.glsl").toString(), 
                                   "graphicparser\\src\\main\\resources\\shaders\\fragment.glsl");

        Scene scene = SceneLoader.loadScene("graphicparser\\src\\main\\resources\\Scene\\scene.json");
        cameraPos = scene.getCameraPos();
        cameraFront = scene.getCameraFront();
        cameraRight = scene.getCameraRight();
        cameraUp = scene.getCameraUp();

        updateCameraDirection();

        parametricCurveOn = scene.isParametricCurveOn();

        List<Mesh> meshes1 = OBJLoader.loadOBJWithMultipleMeshes("graphicparser\\src\\main\\resources\\obj\\Suzanne.obj");
        List<Mesh> meshes2 = OBJLoader.loadOBJWithMultipleMeshes("graphicparser\\src\\main\\resources\\obj\\Pikachu.obj");
        List<Mesh> meshes3 = OBJLoader.loadOBJWithMultipleMeshes("graphicparser\\src\\main\\resources\\obj\\Pokebola.obj");

        Objeto obj1 = createObject(scene.getObj1StartPosition(), meshes1);
        Objeto obj2 = createObject(scene.getObj2StartPosition(), meshes2);
        Objeto obj3 = createObject(scene.getObj3StartPosition(), meshes3);
        List<Objeto> objetos = Arrays.asList(obj1, obj2, obj3);

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
        
            shader.setUniform("view", viewMatrix);
            shader.setUniform("projection", projectionMatrix);
            shader.setUniform("lightPos", lightPos);
            shader.setUniform("lightColor", lightColor);
            shader.setUniform("cameraPos", cameraPos);
        
            renderObjects(objetos, shader, angleX, angleY, angleZ, viewMatrix);
        
            GL30.glBindVertexArray(0);
        
            GLFW.glfwSwapBuffers(janela.getWindowHandle());
        }

        shader.limpar();
        janela.limpar();
    }

    public static void renderObjects(List<Objeto> objetos, Shader shader, float angleX, float angleY, float angleZ, Matrix4f viewMatrix) {
        for (Objeto obj : objetos) {
            obj.modelMatrix = new Matrix4f();

            obj.modelMatrix.translate(obj.getPosition());
            
            if (obj.isSelected()) {

                if (parametricCurveOn) {
                    float t = (float) (System.nanoTime() * 1e-9);
                    obj.setPosition(getParametricCurvePosition(t));
                    obj.updateModelMatrix();
                }

                obj.modelMatrix
                .rotateX(angleX)
                .rotateY(angleY)
                .rotateZ(angleZ);
            } 
    
            shader.setUniform("model", obj.modelMatrix);
            shader.setUniform("view", viewMatrix);
            shader.setUniform("cameraPos", cameraPos);
    
            for (Mesh mesh : obj.getMeshes()) {
                Material meshMaterial = mesh.getMaterial();
                shader.setUniform("ka", meshMaterial.getKa());
                shader.setUniform("kd", meshMaterial.getKd());
                shader.setUniform("ks", meshMaterial.getKs());
                shader.setUniform("q", meshMaterial.getQ());
                if (meshMaterial.getTextureId() != -1) {
                    GL30.glBindTexture(GL30.GL_TEXTURE_2D, mesh.getMaterial().getTextureId());
                }
                GL30.glBindVertexArray(mesh.getVao());
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
            }
    
            GL30.glBindVertexArray(0);
        }
    }

    
    public static void key_callback(long window, int key, int scancode, int action, int mods, List<Objeto> objects) {
        if (key == GLFW.GLFW_KEY_1 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, 0);
        } else if (key == GLFW.GLFW_KEY_2 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, 1);
        } else if (key == GLFW.GLFW_KEY_3 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, 2);
        } else if (key == GLFW.GLFW_KEY_5 && action == GLFW.GLFW_PRESS) {
            setSelectedObj(objects, -1);
        }
    
        if (key == GLFW.GLFW_KEY_W && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            pitch += rotationSpeed;
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_S && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            pitch -= rotationSpeed;
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_A && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            yaw += rotationSpeed;
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_D && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            yaw -= rotationSpeed;
            updateCameraDirection();
        }
    
        if (key == GLFW.GLFW_KEY_Q && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            distance *= 1.1f;
            updateCameraDirection();
        } else if (key == GLFW.GLFW_KEY_E && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
            distance *= 0.9f;
            updateCameraDirection();
        }

        if (key == GLFW.GLFW_KEY_SPACE && action == GLFW.GLFW_PRESS) {
            parametricCurveOn = !parametricCurveOn;
        }
        
        if (objSelecionado != null) {
            if (key == GLFW.GLFW_KEY_UP && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().y += 0.1f;
                objSelecionado.updateModelMatrix();
            } else if (key == GLFW.GLFW_KEY_DOWN && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().y -= 0.1f;
                objSelecionado.updateModelMatrix();
            } else if (key == GLFW.GLFW_KEY_LEFT && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().x -= 0.1f;
                objSelecionado.updateModelMatrix();
            } else if (key == GLFW.GLFW_KEY_RIGHT && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().x += 0.1f;
                objSelecionado.updateModelMatrix();
            } else if (key == GLFW.GLFW_KEY_PAGE_UP && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().z += 0.1f;
            } else if (key == GLFW.GLFW_KEY_PAGE_DOWN && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)) {
                objSelecionado.getPosition().z -= 0.1f;
            }
            if (objSelecionado != null) {
                if (key == GLFW.GLFW_KEY_KP_ADD && action == GLFW.GLFW_PRESS) {
                    objSelecionado.setScale(objSelecionado.getScale() + scaleStep);
                    objSelecionado.updateModelMatrix();
                } else if (key == GLFW.GLFW_KEY_KP_SUBTRACT && action == GLFW.GLFW_PRESS) {
                    float newScale = Math.max(0.1f, objSelecionado.getScale() - scaleStep);
                    objSelecionado.setScale(newScale);
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
    
    private static void updateCameraDirection() {
        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);
    
        Quaternionf pitchQuat = new Quaternionf().rotateAxis(pitchRadians, 1.0f, 0.0f, 0.0f);
        Quaternionf yawQuat = new Quaternionf().rotateAxis(yawRadians, 0.0f, 1.0f, 0.0f);
    
        Quaternionf rotation = yawQuat.mul(pitchQuat);
    
        Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);
    
        rotation.transform(direction);
    
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
    }

    private static Objeto createObject( Vector3f vector, List<Mesh> meshes) {
        Objeto objeto = new Objeto(vector);
        for (Mesh mesh : meshes) {
            objeto.addMesh(mesh);
        }
        return objeto;
    }

    private static Vector3f getParametricCurvePosition(float t) {
        float radius = 5.0f;
        float height = 1.0f;
        float speed = 0.3f;
    
        t *= speed;
    
        t = (float) (t % (2 * Math.PI));
    
        float x = radius * (float) Math.cos(t);
        float y = radius * (float) Math.sin(t);
        float z = height * t;
    
        return new Vector3f(x, y, z);
    }
}
