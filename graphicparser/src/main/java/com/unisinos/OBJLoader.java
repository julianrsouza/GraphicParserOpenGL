package com.unisinos;

import org.joml.Vector3f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OBJLoader {

    public static List<Mesh> loadOBJWithMultipleMeshes(String filePath) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Float> vBuffer = new ArrayList<>();
        List<Mesh> meshes = new ArrayList<>();
        Map<String, Material> materials = new HashMap<>();
        Vector3f color = new Vector3f(1.0f, 0.0f, 0.0f);
        Material currentMaterial = null;  // Inicialize com null (sem material padrão)

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String mtlFilePath = null;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "mtllib":
                        mtlFilePath = tokens[1];
                        System.out.println(mtlFilePath); 
                        if (mtlFilePath != null) {
                            loadMTL(mtlFilePath, materials);
                        }
                        break;
                    case "usemtl":
                        if (!vBuffer.isEmpty()) {
                            // Adiciona o mesh anterior com o material
                            if (currentMaterial != null) {
                                meshes.add(createMeshFromBuffer(vBuffer, currentMaterial)); // Adiciona o mesh com material
                            } else {
                                // Caso não haja material, você pode adicionar um default ou gerenciar esse caso
                                System.out.println("Warning: Material is null for mesh.");
                                meshes.add(createMeshFromBuffer(vBuffer, new Material("default"))); // Usando material default
                            }
                            vBuffer.clear();
                        }
                        currentMaterial = materials.get(tokens[1]);  // Define o material atual
                        break;
                    case "o":
                    case "g":
                        if (!vBuffer.isEmpty()) {
                            meshes.add(createMeshFromBuffer(vBuffer, currentMaterial));
                            vBuffer.clear();
                        }
                        break;
                    case "v":
                        vertices.add(new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                        ));
                        break;
                    case "vt":
                        texCoords.add(new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                        ));
                        break;
                    case "vn":
                        normals.add(new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                        ));
                        break;
                    case "f":
                        processFace(tokens, vertices, texCoords, normals, vBuffer, color);
                        break;
                }
            }

            if (mtlFilePath != null) {
                loadMTL(mtlFilePath, materials);
            }

            // Finalize a última malha
            if (!vBuffer.isEmpty()) {
                meshes.add(createMeshFromBuffer(vBuffer, currentMaterial));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo OBJ: " + e.getMessage());
        }

        return meshes;
    }

    private static void loadMTL(String mtlFilePath, Map<String, Material> materials) {
        try (BufferedReader reader = new BufferedReader(new FileReader(mtlFilePath))) {
            String line;
            Material currentMaterial = null;
    
            while ((line = reader.readLine()) != null) {
                line = line.trim();  // Remove os espaços à esquerda e à direita

                if (line.isEmpty()) {
                    continue;  // Ignora linhas vazias
                }
                String[] tokens = line.split("\\s+");
                System.out.println(tokens[0]);
                switch (tokens[0]) {
                    case "newmtl":
                        currentMaterial = new Material(tokens[1]);
                        materials.put(tokens[1], currentMaterial);
                        break;
                    case "Ns":
                        if (currentMaterial != null) {
                            currentMaterial.setQ(Float.parseFloat(tokens[1]));
                        }
                        break;
                    case "Ka":
                        if (currentMaterial != null) {
                            currentMaterial.setKa(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                            );
                        }
                        break;
                    case "Kd":
                        if (currentMaterial != null) {
                            currentMaterial.setKd(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                            );
                        }
                        break;
                    case "Ks":
                        if (currentMaterial != null) {
                            currentMaterial.setKs(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                            );
                        }
                        break;
                    case "map_Kd":
                        if (currentMaterial != null) {
                            String texturePath = tokens[1];
                            System.out.println(texturePath);
                            // Carregar a textura e associar ao material
                            int textureId = TextureLoader.loadTexture(texturePath);
                            currentMaterial.setTextureId(textureId);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo MTL: " + e.getMessage());
        }
    }
    

    private static void processFace(
        String[] tokens, 
        List<Vector3f> vertices, 
        List<Vector2f> texCoords, 
        List<Vector3f> normals, 
        List<Float> vBuffer, 
        Vector3f color
    ) {
        for (int i = 1; i < tokens.length; i++) {
            String[] parts = tokens[i].split("/");
            int vi = Integer.parseInt(parts[0]) - 1; // Índice do vértice
            int ti = Integer.parseInt(parts[1]) - 1; // Índice da textura
            int ni = Integer.parseInt(parts[2]) - 1; // Índice da normal
    
            // Adiciona as coordenadas do vértice
            Vector3f vertex = vertices.get(vi);
            vBuffer.add(vertex.x);
            vBuffer.add(vertex.y);
            vBuffer.add(vertex.z);
    
            // Adiciona a cor
            vBuffer.add(color.x);
            vBuffer.add(color.y);
            vBuffer.add(color.z);
    
            // Adiciona as coordenadas da textura
            Vector2f texCoord = texCoords.get(ti);
            vBuffer.add(texCoord.x);
            vBuffer.add(texCoord.y);
    
            // Adiciona as coordenadas da normal
            Vector3f normal = normals.get(ni);
            vBuffer.add(normal.x);
            vBuffer.add(normal.y);
            vBuffer.add(normal.z);
        }
    }
    

    private static Mesh createMeshFromBuffer(List<Float> vBuffer, Material material) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vBuffer.size());
        for (Float f : vBuffer) {
            buffer.put(f);
        }
        buffer.flip();
    
        int VAO = GL30.glGenVertexArrays();
        int VBO = GL30.glGenBuffers();
    
        GL30.glBindVertexArray(VAO);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, VBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
    
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 11 * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(0);
    
        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 11 * Float.BYTES, 3 * Float.BYTES);
        GL30.glEnableVertexAttribArray(1);
    
        GL30.glVertexAttribPointer(2, 2, GL30.GL_FLOAT, false, 11 * Float.BYTES, 6 * Float.BYTES);
        GL30.glEnableVertexAttribArray(2);
    
        GL30.glVertexAttribPointer(3, 3, GL30.GL_FLOAT, false, 11 * Float.BYTES, 8 * Float.BYTES);
        GL30.glEnableVertexAttribArray(3);
    
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    
        MemoryUtil.memFree(buffer);
    
        return new Mesh(VAO, vBuffer.size() / 11, material);
    }
}