package com.assassin.origins.ui.request.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DataListAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mViewBinder: ViewBinder<T>? = null
    private val mData = ArrayList<T>()

    fun updateData(data: List<T>?) {
        if (data == null) {
            return
        }
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setViewBinder(viewBinder: ViewBinder<T>): DataListAdapter<T> {
        mViewBinder = viewBinder
        return this
    }

    override fun getItemViewType(position: Int): Int {
        checkAssert()
        return mViewBinder!!.getItemViewType(mData[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        checkAssert()
        return mViewBinder!!.createViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        checkAssert()
        mViewBinder!!.bindView(holder, mData[position], position)
    }

    private fun checkAssert() {
        if (mViewBinder == null) {
            throw RuntimeException("ViewBinder must not be null")
        }
    }

}