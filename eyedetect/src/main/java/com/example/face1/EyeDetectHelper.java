package com.example.face1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;


import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EyeDetectHelper implements IEyeDetectorManager, ImageAnalysis.Analyzer {
    private static final String TAG = "EyeDetectHelper";

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java4");
    }

    private Context mContext;
    private CloseRemindListener listener;
    private OrientationDetector orientationDetector;
    private boolean needRotation = true;
    private FaceTracker faceTracker;
    private File mCascadeFile;
    private int lensFacing = CameraSelector.LENS_FACING_FRONT;
    private ProcessCameraProvider cameraProvider;
    private ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public EyeDetectHelper(EyeDetectorManager.Builder builder) {
        this.mContext = builder.getContext();
        this.listener = builder.getListener();
        orientationDetector = new OrientationDetector(mContext, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setUpCamera() {
        final ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(mContext);
        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = future.get();
                    bindCameraUseCases();
                } catch (ExecutionException | IllegalArgumentException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    private void bindCameraUseCases() {
        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) mContext, selector, getAnalyzer());
    }


    private ImageAnalysis getAnalyzer() {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor, this);
        return imageAnalysis;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void start() {
        if (null != orientationDetector && orientationDetector.canDetectOrientation())
            orientationDetector.enable();
        initCascade();
        setUpCamera();
    }

    private void initCascade() {
        try {
            // load cascade file from application resources
            InputStream is = mContext.getResources().openRawResource(R.raw.haar);
            File cascadeDir = mContext.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "haar.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            faceTracker = new FaceTracker(mCascadeFile.getAbsolutePath());
            faceTracker.start();
            cascadeDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void resume() {
        if (null != orientationDetector && orientationDetector.canDetectOrientation())
            orientationDetector.enable();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void pause() {
        if (orientationDetector != null)
            orientationDetector.disable();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void stop() {
        if (orientationDetector != null)
            orientationDetector.disable();
    }

    @Override
    public void release() {
        cameraExecutor.shutdown();
        faceTracker.release();
    }

    private int analyzeIndex = 0;
    private int ANALYZE_INTERVAL = 5;//降低cpu占用，默认每5帧检测一次

    @Override
    public void analyze(@NonNull ImageProxy image) {
        try {
            if (++analyzeIndex % ANALYZE_INTERVAL == 0) {
                byte[] bytes = ImageUitl.yuv420ToNv21(image);
                float distance = faceTracker.detectEyesDistance(bytes, image.getWidth(), image.getHeight(), 0, 2.84f, needRotation);
                detectDistance(distance);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            image.close();
        }
    }

    private int index = 0;
    private int freeIndex = 0;//无效结果
    private final int INTERVAL = 3;//取3帧有效的平均值判断
    private final int FREE_INTERVAL = 10;//连续10帧未检测到，默认关闭提示
    private int sum = 0;

    /**
     * 根据检测到的距离和设置条件，来确定是否发出警报
     *
     * @param distance
     */
    private void detectDistance(float distance) {
        if (distance == -1.0) {
            if (++freeIndex % FREE_INTERVAL == 0) {
//              todo 连续未检测到脸 暂时消失
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG,  "连续-1 未检测到眼睛 hideView");
                        if (listener != null) {
                            listener.onHideRemindView();
                        }
                    }
                });
                index = 0;
                sum = 0;
                freeIndex = 0;
            }
        } else {
            Log.e(TAG,distance+"");
            freeIndex = 0;
            if (++index % INTERVAL == 0) {
                sum += distance;
                distance = sum / INTERVAL;
                if (distance < 20.0) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onShowRemindView();
                            }
                        }
                    });
                } else if (distance > 21.0) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onHideRemindView();
                            }
                        }
                    });
                }
                sum = 0;
                Log.e(TAG, distance + ":INTERVAL");
            } else {
                sum += distance;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected class OrientationDetector extends OrientationEventListener {

        public OrientationDetector(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN)
                return;
            //只检测是否有四个角度的改变
            if (orientation > 345 || orientation < 15) { //0度
                //orientation = 0;
                needRotation = true;
            } else if (orientation > 75 && orientation < 105) { //90度
                //orientation = 90;
                needRotation = true;
            } else if (orientation > 165 && orientation < 195) { //180度
                //orientation = 180;
                needRotation = false;
            } else if (orientation > 255 && orientation < 285) { //270度
                //orientation = 270;
                needRotation = false;
            } else {
                return;
            }
        }
    }

}
