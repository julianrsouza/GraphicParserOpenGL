package com.unisinos;

import org.joml.Vector3f;

public class Scene {
    private Vector3f obj1StartPosition;
    private Vector3f obj2StartPosition;
    private Vector3f obj3StartPosition;
    private boolean isParametricCurveOn;
    private Vector3f cameraPos;
    private Vector3f cameraFront;
    private Vector3f cameraUp;
    private Vector3f cameraRight;

    public Vector3f getObj1StartPosition() {
        return obj1StartPosition;
    }
    public void setObj1StartPosition(Vector3f obj1StartPosition) {
        this.obj1StartPosition = obj1StartPosition;
    }
    public Vector3f getObj2StartPosition() {
        return obj2StartPosition;
    }
    public void setObj2StartPosition(Vector3f obj2StartPosition) {
        this.obj2StartPosition = obj2StartPosition;
    }
    public Vector3f getObj3StartPosition() {
        return obj3StartPosition;
    }
    public void setObj3StartPosition(Vector3f obj3StartPosition) {
        this.obj3StartPosition = obj3StartPosition;
    }
    public boolean isParametricCurveOn() {
        return isParametricCurveOn;
    }
    public void setParametricCurveOn(boolean isParametricCurveOn) {
        this.isParametricCurveOn = isParametricCurveOn;
    }
    public Vector3f getCameraPos() {
        return cameraPos;
    }
    public void setCameraPos(Vector3f cameraPos) {
        this.cameraPos = cameraPos;
    }
    public Vector3f getCameraFront() {
        return cameraFront;
    }
    public void setCameraFront(Vector3f cameraFront) {
        this.cameraFront = cameraFront;
    }
    public Vector3f getCameraUp() {
        return cameraUp;
    }
    public void setCameraUp(Vector3f cameraUp) {
        this.cameraUp = cameraUp;
    }
    public Vector3f getCameraRight() {
        return cameraRight;
    }
    public void setCameraRight(Vector3f cameraRight) {
        this.cameraRight = cameraRight;
    }

}
