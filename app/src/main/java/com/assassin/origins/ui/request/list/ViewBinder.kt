package com.assassin.origins.ui.request.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class ViewBinder<T> {

    abstract fun createViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun bindView(holder: RecyclerView.ViewHolder, data: T, position: Int)

    fun getItemViewType(data: T): Int {
        return 0
    }
}