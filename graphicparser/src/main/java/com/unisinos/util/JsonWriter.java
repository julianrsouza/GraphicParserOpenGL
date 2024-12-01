package com.unisinos.util;

import java.io.File;
import java.io.IOException;

import org.joml.Vector3f;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.unisinos.Scene;

public class JsonWriter {
    
     public static void main(String[] args) {
        // Configurando o objeto Scene
        Scene scene = new Scene();
        scene.setObj1StartPosition(new Vector3f(-3.0f, -3.0f, -3.0f));
        scene.setObj2StartPosition(new Vector3f(0.0f, -2.0f, 0.0f));
        scene.setObj3StartPosition(new Vector3f(3.0f, 3.0f, 3.0f));
        scene.setParametricCurveOn(true);
        scene.setCameraPos(new Vector3f(0.0f, 0.0f, 5.0f));
        scene.setCameraFront(new Vector3f(0.0f, 0.0f, -1.0f));
        scene.setCameraUp(new Vector3f(0.0f, 1.0f, 0.0f));
        scene.setCameraRight(new Vector3f(0.0f, 1.0f, 0.0f));

        // Configurando o ObjectMapper com suporte para Vector3f
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Vector3f.class, new Vector3fSerializer());
        objectMapper.registerModule(module);

        // Escrevendo o arquivo JSON
        File arquivoJson = new File("graphicparser\\src\\main\\resources\\Scene\\scene.json");
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(arquivoJson, scene);
            System.out.println("Arquivo JSON criado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
