package com.assassin.origins.splash.api

import com.assassin.origins.splash.api.model.Photo
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {

    @GET("/photos")
    fun getPhotoList(): Observable<List<Photo>>

    @GET("/photos/random")
    fun getRandomPhoto(): Observable<Photo>
}