package com.example.base.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : ViewModel> : Fragment() {
    private var isFirst: Boolean = true

    var mContext: Context? = null

    val mViewModel: VM by lazy {
        ViewModelProvider(getOwner()).get(getViewModelClass())
    }

    val fragmentProvider :ViewModelProvider by lazy{
        ViewModelProvider(this)
    }

    val activityProvider:ViewModelProvider by lazy{
        ViewModelProvider(requireActivity())
    }

    val applicationProvider:ViewModelProvider by lazy{
        (context?.applicationContext as BaseApplication).getAppViewModelProvider()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun getViewModelClass(): Class<VM> {
        val actualTypeArguments =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

        return actualTypeArguments[0] as Class<VM>
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initObserver()
        initData()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun <T:ViewModel> getFragmentScopeViewModel(modelClass:Class<T>):T{
        return applicationProvider.get(modelClass)
    }

    open fun getApplicationFactory(activity: Activity): ViewModelProvider.Factory {
        checkActivity(this)
        val application: Application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    open fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    open fun checkActivity(fragment: Fragment) {
         fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initObserver()

    abstract fun lazyLoadData()

    private fun initData() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false
        }
    }

    protected open fun getOwner(): ViewModelStoreOwner {
        return this
    }

}