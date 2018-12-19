package com.assassin.origins.ui.list

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assassin.origins.R
import com.assassin.origins.core.viewmodel.DataViewModel
import com.assassin.origins.ui.base.BaseActivity
import kotlinx.android.synthetic.main.ac_request_list.*

abstract class RequestListActivity<T> : BaseActivity() {

    private var mViewModel: DataViewModel<T>? = null
    private var mAdapter: DataListAdapter<T>? = null

    protected fun getViewModel(): DataViewModel<T>? {
        return mViewModel
    }

    final override fun getLayout(): Int {
        return R.layout.ac_request_list
    }

    protected abstract fun createViewModel(): DataViewModel<T>

    protected abstract fun createViewBinder(): ViewBinder<T>

    protected fun onDataChanged(t: List<T>) {
        mAdapter?.updateData(t)
    }

    protected fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(recycler_view) {
            layoutManager = createLayoutManager()
            mAdapter = DataListAdapter<T>().setViewBinder(createViewBinder())
            adapter = mAdapter
        }
        mViewModel = createViewModel()
        mViewModel?.loadData()
        mViewModel?.getData()?.observe(this, Observer {
            onDataChanged(it)
        })
    }
}