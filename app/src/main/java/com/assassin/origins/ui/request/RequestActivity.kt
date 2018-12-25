package com.assassin.origins.ui.request

import android.os.Bundle
import androidx.lifecycle.Observer
import com.assassin.origins.core.viewmodel.DataViewModel
import com.assassin.origins.ui.base.BaseActivity

abstract class RequestActivity<T> : BaseActivity() {

    private var mViewModel: DataViewModel<T>? = null

    protected fun getViewModel(): DataViewModel<T>? {
        return mViewModel
    }

    protected abstract fun createViewModel(): DataViewModel<T>

    protected open fun onDataChanged(data: T?) {
        when (data) {
            null -> showEmpty()
            else -> {
                showContent()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLoading()
        mViewModel = createViewModel()
        mViewModel?.loadData()
        mViewModel?.getData()?.observe(this, Observer {
            onDataChanged(it)
        })
    }

    protected fun refresh(loading: Boolean) {
        if (loading) {
            showLoading()
        }
        mViewModel?.loadData()
    }
}