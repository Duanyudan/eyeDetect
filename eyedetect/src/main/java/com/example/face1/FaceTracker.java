package com.example.face1;


public class FaceTracker {
    private long mNativeObject = 0;

    public FaceTracker(String model) {
        mNativeObject = nativeCreateObject(model);
    }

    public float detectEyesDistance(byte[] data, int width, int height, int cameraType, float focalLength, boolean needRotation) {
        return detectEyesDistance(mNativeObject, data, width, height, cameraType, focalLength, needRotation);
    }


    public native float detectEyesDistance(long mNativeObject, byte[] data, int width, int height, int cameraType, float focalLength, boolean needRotation);

    public native long nativeCreateObject(String model);

    public void start() {
        nativeStart(mNativeObject);
    }

    public native void nativeStart(long mNativeObject);

    public void release() {
        nativeDestoryObject(mNativeObject);
        mNativeObject = 0;
    }

    public native void nativeDestoryObject(long mNativeObject);

}
