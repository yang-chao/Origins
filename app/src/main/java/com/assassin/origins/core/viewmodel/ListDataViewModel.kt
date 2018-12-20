package com.assassin.origins.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class ListDataViewModel<T>(application: Application) : AndroidViewModel(application) {

    protected var data: MutableLiveData<List<T>> = MutableLiveData()

    fun getData(): LiveData<List<T>> {
        return data
    }

    abstract fun loadData()
}