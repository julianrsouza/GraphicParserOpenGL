package com.unisinos;

import java.io.File;
import java.io.IOException;

import org.joml.Vector3f;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.unisinos.util.Vector3fDeserializer;

public class SceneLoader {
    
    public static Scene loadScene(String scenePath) {
        Scene scene = new Scene();

        try {
            // Configurando o ObjectMapper com suporte para Vector3f
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Vector3f.class, new Vector3fDeserializer());
            objectMapper.registerModule(module);

            // Lendo e deserializando o arquivo JSON para a classe Scene
            scene = objectMapper.readValue(new File(scenePath), Scene.class);

        } catch (IOException e) {
            System.err.println("Erro ao carregar a cena: " + e.getMessage());
            e.printStackTrace();
        }

        return scene;
    }
}
