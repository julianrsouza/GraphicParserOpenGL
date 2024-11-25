package com.unisinos;

import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureLoader {

    public static int loadTexture(String filePath) {
        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        IntBuffer channels = MemoryUtil.memAllocInt(1);

        STBImage.stbi_set_flip_vertically_on_load(true);

        // Carrega a textura em memória
        ByteBuffer image = STBImage.stbi_load(filePath, width, height, channels, 4);
        if (image == null) {
            MemoryUtil.memFree(width);
            MemoryUtil.memFree(height);
            MemoryUtil.memFree(channels);
            throw new RuntimeException("Failed to load texture: " + filePath + 
                                        "\nReason: " + STBImage.stbi_failure_reason());
        }

        // Gera e configura a textura
        int textureId = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);

        GL30.glTexImage2D(
            GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA,
            width.get(0), height.get(0),
            0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, image
        );

        // Configura filtros e modo de wrapping
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);

        // Libera memória alocada pela STB e pelo Java
        STBImage.stbi_image_free(image);
        MemoryUtil.memFree(width);
        MemoryUtil.memFree(height);
        MemoryUtil.memFree(channels);

        return textureId;
    }
}
