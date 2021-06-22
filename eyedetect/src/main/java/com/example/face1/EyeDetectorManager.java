package com.example.face1;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

public final class EyeDetectorManager {

    private final static String TAG = EyeDetectorManager.class.getSimpleName();

    private static IEyeDetectorManager mEyeDetectorManager;

    private EyeDetectorManager() {
    }

    public static final class Builder {

        private CloseRemindListener listener;

        private Context context;

        public CloseRemindListener getListener() {
            return listener;
        }


        public Context getContext() {
            return context;
        }

        public Builder() {
        }

        public Builder closeRemindListener(CloseRemindListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        public IEyeDetectorManager build() {
            if (isX86()) {
                return null;
            }
            mEyeDetectorManager = new EyeDetectHelper(this);
            return mEyeDetectorManager;
        }
    }

    static boolean isX86() {
        String[] abis = new String[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for (String abi : abis) {
            abiStr.append(abi);
            abiStr.append(',');
        }
        Log.i(TAG, "CPU架构: " + abiStr.toString());
        if (abiStr.toString().contains("x86")) {
            return true;
        }
        return false;
    }
}
