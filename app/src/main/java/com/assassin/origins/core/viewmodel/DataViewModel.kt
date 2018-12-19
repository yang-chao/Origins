package com.assassin.origins.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class DataViewModel<T> : ViewModel() {

    private var requestData: MutableLiveData<List<T>> = MutableLiveData()

    fun getData(): LiveData<List<T>> {
        return requestData
    }

    abstract fun loadData()
}