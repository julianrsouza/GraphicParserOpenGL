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

public class OBJLoader {

    public static Mesh loadSimpleOBJ(String filePath) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Float> vBuffer = new ArrayList<>();

        Vector3f color = new Vector3f(1.0f, 0.0f, 0.0f);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
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
                        for (int i = 1; i < tokens.length; i++) {
                            String[] parts = tokens[i].split("/");
                            int vi = Integer.parseInt(parts[0]) - 1;
                            int ti = Integer.parseInt(parts[1]) - 1;
                            int ni = Integer.parseInt(parts[2]) - 1;

                            Vector3f vertex = vertices.get(vi);
                            vBuffer.add(vertex.x);
                            vBuffer.add(vertex.y);
                            vBuffer.add(vertex.z);

                            vBuffer.add(color.x);
                            vBuffer.add(color.y);
                            vBuffer.add(color.z);

                            Vector2f texCoord = texCoords.get(ti);
                            vBuffer.add(texCoord.x);
                            vBuffer.add(texCoord.y);

                            Vector3f normal = normals.get(ni);
                            vBuffer.add(normal.x);
                            vBuffer.add(normal.y);
                            vBuffer.add(normal.z);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo OBJ: " + e.getMessage());
            return null;
        }

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

        int vertexCount = vBuffer.size() / 11;

        return new Mesh(VAO, vertexCount);
    }

    public static List<Mesh> loadOBJWithMultipleMeshes(String filePath) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Float> vBuffer = new ArrayList<>();
        List<Mesh> meshes = new ArrayList<>();
        
        Vector3f color = new Vector3f(1.0f, 0.0f, 0.0f);
        String currentMaterial = "default";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentGroupName = "default";
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "o":
                    case "g":
                        if (!vBuffer.isEmpty()) {
                            meshes.add(createMeshFromBuffer(vBuffer));
                            vBuffer.clear();
                        }
                        currentGroupName = tokens.length > 1 ? tokens[1] : "default";
                        break;
    
                    case "usemtl":
                        if (!vBuffer.isEmpty()) {
                            meshes.add(createMeshFromBuffer(vBuffer));
                            vBuffer.clear();
                        }
                        currentMaterial = tokens[1];
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
    
                    case "vn": // Normais
                        normals.add(new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                        ));
                        break;
    
                    case "f":
                            for (int i = 1; i < tokens.length; i++) {
                                String[] parts = tokens[i].split("/");
                                int vi = Integer.parseInt(parts[0]) - 1;
                                int ti = Integer.parseInt(parts[1]) - 1;
                                int ni = Integer.parseInt(parts[2]) - 1;
                                Vector3f vertex = vertices.get(vi);
                                vBuffer.add(vertex.x);
                                vBuffer.add(vertex.y);
                                vBuffer.add(vertex.z);

                                vBuffer.add(color.x);
                                vBuffer.add(color.y);
                                vBuffer.add(color.z);

                                Vector2f texCoord = texCoords.get(ti);
                                vBuffer.add(texCoord.x);
                                vBuffer.add(texCoord.y);

                                if (ni >= 0 && ni < normals.size()) {
                                    Vector3f normal = normals.get(ni);
                                    vBuffer.add(normal.x);
                                    vBuffer.add(normal.y);
                                    vBuffer.add(normal.z);
                                } else {
                                    vBuffer.add(0.0f);
                                    vBuffer.add(0.0f);
                                    vBuffer.add(1.0f);
                                }
                            }
                        break;
                }
            }
    
            // Finalize a última malha
            if (!vBuffer.isEmpty()) {
                meshes.add(createMeshFromBuffer(vBuffer));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo OBJ: " + e.getMessage());
        }
    
        return meshes;
    }
    
    private static Mesh createMeshFromBuffer(List<Float> vBuffer) {
        // Converter o vBuffer para um FloatBuffer
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vBuffer.size());
        for (Float f : vBuffer) {
            buffer.put(f);
        }
        buffer.flip();
    
        // Criar VAO e VBO
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
    
        // Retornar a malha criada
        return new Mesh(VAO, vBuffer.size() / 11);
    }
    
}
