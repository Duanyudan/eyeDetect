package com.example.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;


/**
 * 文件描述：
 * 作者：damon
 * 创建日期：2017/4/10
 */

public class NetworkUtil {
    public static final int NETWORK_TYPE_WIFI = 100;
    public static final int NETWORK_TYPE_MOBILE = 101;
    public static final int NETWORK_TYPE_OTHER = 102;

    //判断网络连接是否可用
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //判断WiFi是否打开
    public static int getNetWorkType(Context context) {
        int networkType = NETWORK_TYPE_WIFI;
        if (NetworkUtil.isWiFi(context)) {
            networkType = NETWORK_TYPE_WIFI;
        } else if (NetworkUtil.isMobile(context)) {
            networkType = NETWORK_TYPE_MOBILE;
        } else {
            networkType = NETWORK_TYPE_OTHER;
        }
        return networkType;
    }


    //判断WiFi是否打开
    public static boolean isWiFi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    //判断移动数据是否打开
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Log.i("Avalible", "Process:" + exitValue);
            return (exitValue == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断手机4G网络是否打开
     */
    public static boolean isMobileEnable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Method getMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            getMobileDataEnabledMethod.setAccessible(true);
            return (Boolean) getMobileDataEnabledMethod.invoke(connectivityManager);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //没有网络连接
    private static final int NETWORK_NONE = -1;
    //手机网络数据连接类型
    private static final int NETWORK_2G = 0;
    private static final int NETWORK_3G = 1;
    private static final int NETWORK_3G_W_CDMA = 2;
    private static final int NETWORK_4G = 3;
    private static final int NETWORK_MOBILE = 4;
    //没有网络连接
    private static final String TEXT_NETWORK_NONE = "none";
    //wifi连接
    private static final String TEXT_NETWORK_WIFI = "wifi";
    //手机网络数据连接类型

    /**
     * 获取当前网络连接类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager)
            return TEXT_NETWORK_NONE;

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return TEXT_NETWORK_NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return TEXT_NETWORK_WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        int mobileRadioType = getMobileRadioType(context);
        switch (mobileRadioType) {
            case NETWORK_NONE:
                return TEXT_NETWORK_NONE;
            case NETWORK_2G:
                return "2G";
            case NETWORK_3G:
            case NETWORK_3G_W_CDMA:
                return "3G";
            case NETWORK_4G:
                return "4G";
            default:
                return "mobile";
        }
    }

    private static int getMobileRadioType(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager)
            return NETWORK_NONE;
        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                            return NETWORK_3G_W_CDMA;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_4G;
                        default:
                            return NETWORK_MOBILE;
                    }
                }
        }
        return NETWORK_NONE;
    }
}
