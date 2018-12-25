package com.assassin.origins.splash.core.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import com.assassin.origins.core.viewmodel.DataViewModel
import com.assassin.origins.splash.api.model.Photo
import com.assassin.origins.splash.core.repository.PhotoRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PhotoViewModel(application: Application) : DataViewModel<Photo>(application) {

    private val repo: PhotoRepository = PhotoRepository()

    @SuppressLint("CheckResult")
    override fun loadData() {
        repo.getRandomPhoto()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data.postValue(it) }, { data.postValue(null) })
    }
}