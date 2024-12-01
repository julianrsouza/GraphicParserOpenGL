package com.unisinos.util;

import java.io.IOException;

import org.joml.Vector3f;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class Vector3fSerializer extends JsonSerializer<Vector3f> {
    @Override
    public void serialize(Vector3f vector, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("x", vector.x);
        gen.writeNumberField("y", vector.y);
        gen.writeNumberField("z", vector.z);
        gen.writeEndObject();
    }
}
