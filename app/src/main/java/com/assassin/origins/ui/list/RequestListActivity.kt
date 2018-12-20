package com.assassin.origins.ui.list

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assassin.origins.R
import com.assassin.origins.core.viewmodel.ListDataViewModel
import com.assassin.origins.ui.base.BaseActivity
import kotlinx.android.synthetic.main.ac_request_list.*

abstract class RequestListActivity<T> : BaseActivity() {

    private var mViewModel: ListDataViewModel<T>? = null
    private var mAdapter: DataListAdapter<T>? = null

    protected fun getViewModel(): ListDataViewModel<T>? {
        return mViewModel
    }

    final override fun getLayout(): Int {
        return R.layout.ac_request_list
    }

    protected abstract fun createViewModel(): ListDataViewModel<T>

    protected abstract fun createViewBinder(): ViewBinder<T>

    protected fun onDataChanged(list: List<T>?) {
        when {
            list == null -> showError()
            list.isEmpty() -> showEmpty()
            else -> {
                mAdapter?.updateData(list)
                showContent()
            }
        }
    }

    protected fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // start load data from net/local data storage
        showLoading()
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