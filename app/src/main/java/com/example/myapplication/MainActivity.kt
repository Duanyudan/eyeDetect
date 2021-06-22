package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.face1.EyeDetectorManager
import com.example.face1.IEyeDetectorManager
//import com.example.face1.EyeDetectorManager
//import com.example.face1.IEyeDetectorManager
//import com.example.myapplication.TestConst.Companion.string1
//import com.example.myapplication.TestConst.Companion.string2
import com.tbruyelle.rxpermissions2.RxPermissions
//import io.flutter.embedding.android.FlutterActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    var camera2Helper: IEyeDetectorManager? = null

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initEyeDetect()
    }

    @SuppressLint("CheckResult")
    fun initEyeDetect() {
        val rxPermission = RxPermissions(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (this.camera2Helper == null) {
                camera2Helper = EyeDetectorManager.Builder()
                    .context(this)
                    .build()
            }
            camera2Helper?.start()
        } else {
            rxPermission.request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted) {
                        if (camera2Helper == null)
                            camera2Helper = EyeDetectorManager.Builder()
                                .context(this)
                                .build()
                        camera2Helper?.start()
                    }
                }
        }
    }


    override fun onStop() {
        super.onStop()
        camera2Helper?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera2Helper?.release()
    }
}

