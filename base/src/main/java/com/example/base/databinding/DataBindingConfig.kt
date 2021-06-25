package com.example.base.databinding

import android.util.SparseArray
import androidx.lifecycle.ViewModel

class DataBindingConfig(
     val layout: Int,
     val mVariableId: Int? = 0,
     val mStateViewModel: ViewModel? = null
) {

    private val mBindingParams: SparseArray<Any> = SparseArray<Any>()

    fun getBindingParams(): SparseArray<Any> {
        return mBindingParams
    }

    fun addDatabindingParams(variableId: Int, obj: Any): DataBindingConfig {
        mBindingParams[variableId] ?: mBindingParams.put(variableId, obj)
        return this
    }
}