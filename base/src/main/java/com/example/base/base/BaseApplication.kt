package com.example.base.base

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class BaseApplication : Application(), ViewModelStoreOwner {
    private val mAppViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    override fun getViewModelStore() = mAppViewModelStore

    private val mFactory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }

    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, mFactory)
    }
}