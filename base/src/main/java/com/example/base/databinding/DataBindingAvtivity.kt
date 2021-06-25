package com.example.base.databinding

import android.os.Bundle
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.example.base.base.BaseActivity
import com.example.base.base.BaseViewModel

abstract class DataBindingAvtivity<D : ViewDataBinding,VM:ViewModel> : BaseActivity<VM>() {

    lateinit var mBinding: D

    abstract fun getDataBindingConfig():DataBindingConfig

    abstract fun initViewModel()

    abstract fun initObserver()

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()

        val dataBindingConfig=getDataBindingConfig()
        val binding=DataBindingUtil.setContentView<ViewDataBinding>(this,dataBindingConfig.layout)
        binding.lifecycleOwner=this
        binding.setVariable(dataBindingConfig.mVariableId!!,dataBindingConfig.mStateViewModel)
        dataBindingConfig.getBindingParams().forEach { key, value ->
            binding.setVariable(key,value)
        }
        mBinding=binding as D

        initObserver()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}