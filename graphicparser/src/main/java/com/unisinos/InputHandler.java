package com.unisinos;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private long window;
    private boolean[] keys = new boolean[GLFW_KEY_LAST];
    private double mouseX, mouseY;
    private double deltaX, deltaY;
    private boolean firstMouse = true;

    public InputHandler(long window) {
        this.window = window;
        glfwSetKeyCallback(window, (long win, int key, int scancode, int action, int mods) -> {
            if (key >= 0 && key < GLFW_KEY_LAST) {
                keys[key] = action != GLFW_RELEASE;
            }
        });

        glfwSetCursorPosCallback(window, (long win, double xpos, double ypos) -> {
            if (firstMouse) {
                mouseX = xpos;
                mouseY = ypos;
                firstMouse = false;
            }
            deltaX = xpos - mouseX;
            deltaY = ypos - mouseY;
            mouseX = xpos;
            mouseY = ypos;
        });

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public boolean isKeyPressed(int key) {
        return keys[key];
    }

    public double getDeltaX() {
        double dx = deltaX;
        deltaX = 0;
        return dx;
    }

    public double getDeltaY() {
        double dy = deltaY;
        deltaY = 0;
        return dy;
    }

    public void resetMouse() {
        firstMouse = true;
    }
}
