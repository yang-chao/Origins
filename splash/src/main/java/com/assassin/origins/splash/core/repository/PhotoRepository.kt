package com.assassin.origins.splash.core.repository

import com.assassin.origins.splash.api.model.Photo
import com.assassin.origins.splash.core.ApiServiceManager
import io.reactivex.Observable

class PhotoRepository {

    private val apiService = ApiServiceManager.instance.getApiService()

    fun getRandomPhoto(): Observable<Photo> {
        return apiService.getRandomPhoto()
    }
}