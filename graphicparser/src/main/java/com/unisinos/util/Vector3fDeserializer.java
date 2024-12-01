package com.unisinos.util;

import java.io.IOException;

import org.joml.Vector3f;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class Vector3fDeserializer extends JsonDeserializer<Vector3f> {
    
        @Override
    public Vector3f deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // Lendo o JSON como um objeto genérico
        JsonNode node = parser.getCodec().readTree(parser);
        float x = (float) node.get("x").asDouble();
        float y = (float) node.get("y").asDouble();
        float z = (float) node.get("z").asDouble();
        return new Vector3f(x, y, z);
    }
}
