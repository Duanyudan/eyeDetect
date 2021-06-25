package com.example.base.base

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType

open class BaseActivity<VM : ViewModel> : AppCompatActivity() {
    private val mActivityProvider: ViewModelProvider by lazy {
        ViewModelProvider(this)
    }

    val mViewModel: VM by lazy {
        ViewModelProvider(this).get(getViewModelClass())
    }

    private fun getViewModelClass(): Class<VM> {
        val actualTypeArguments =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        return actualTypeArguments[0] as Class<VM>

    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                ("Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call.")
            )
    }

    open fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {
        return mActivityProvider.get(modelClass)
    }

    open fun <T : ViewModel> getAppScopeViewModel(modelClass: Class<T>): T {
        return (application as BaseApplication).getAppViewModelProvider().get(modelClass)
    }


}