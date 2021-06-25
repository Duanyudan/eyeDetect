package com.example.base.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.example.base.base.BaseFragment

abstract class DataBindingFragment<D : ViewDataBinding, VM : ViewModel> : BaseFragment<VM>() {

    lateinit var mBinding: D
    abstract fun getDataBindingConfig(): DataBindingConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBindingConfig = getDataBindingConfig()
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            dataBindingConfig.layout,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.setVariable(dataBindingConfig.mVariableId!!, dataBindingConfig.mStateViewModel)
        dataBindingConfig.getBindingParams().forEach { key, value ->
            binding.setVariable(key, value)
        }
        mBinding = binding as D
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

}