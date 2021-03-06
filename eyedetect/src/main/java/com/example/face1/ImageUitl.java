package com.example.face1;

import android.util.Log;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

public class ImageUitl {
    public static byte[] yuv420ToNv21(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);

        byte[] u = new byte[uSize];
        uBuffer.get(u);

        //每隔开一位替换V，达到VU交替
        int pos = ySize + 1;
        for (int i = 0; i < uSize; i++) {
            if (i % 2 == 0) {
                nv21[pos] = u[i];
                pos += 2;
            }
        }
        return nv21;
    }
//    public static byte[] yuv420ToNv21(ImageProxy image) {
//
//        int width = image.getWidth();
//        int height = image.getHeight();
//        int ySize = width * height;
//        int uvSize = width * height / 4;
//
//        byte[] nv21 = new byte[ySize + uvSize * 2];
//
//        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
//        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
//        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V
//        Log.e("dyd", "ImageUitl  ySize:" + ySize +" size" + nv21.length);
//        Log.e("dyd", "yBuffer" + yBuffer.remaining());
//        int rowStride = image.getPlanes()[0].getRowStride();
//        assert (image.getPlanes()[0].getPixelStride() == 1);
//
//        int pos = 0;
//
//        if (rowStride == width) { // likely
//            yBuffer.get(nv21, 0, ySize);
//            pos += ySize;
//        } else {
//            int yBufferPos = -rowStride; // not an actual position
//            for (; pos < ySize; pos += width) {
//                yBuffer.position(yBufferPos);
//                yBuffer.get(nv21, pos, width);
//                yBufferPos += rowStride;
//            }
//        }
//
//        rowStride = image.getPlanes()[2].getRowStride();
//        int pixelStride = image.getPlanes()[2].getPixelStride();
//
//        assert (rowStride == image.getPlanes()[1].getRowStride());
//        assert (pixelStride == image.getPlanes()[1].getPixelStride());
//
//        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
//            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
//            byte savePixel = vBuffer.get(1);
//            try {
//                vBuffer.put(1, (byte) ~savePixel);
//                if (uBuffer.get(0) == (byte) ~savePixel) {
//                    vBuffer.put(1, savePixel);
//                    vBuffer.position(0);
//                    uBuffer.position(0);
//                    vBuffer.get(nv21, ySize, 1);
//                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());
//
//                    return nv21; // shortcut
//                }
//            } catch (ReadOnlyBufferException ex) {
//                // unfortunately, we cannot check if vBuffer and uBuffer overlap
//            }
//
//            // unfortunately, the check failed. We must save U and V pixel by pixel
//            vBuffer.put(1, savePixel);
//        }
//
//        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
//        // but performance gain would be less significant
//
//        for (int row = 0; row < height / 2; row++) {
//            for (int col = 0; col < width / 2; col++) {
//                int vuPos = col * pixelStride + row * rowStride;
//                nv21[pos++] = vBuffer.get(vuPos);
//                nv21[pos++] = uBuffer.get(vuPos);
//            }
//        }
//
//        return nv21;
//    }
}
