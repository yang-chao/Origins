package com.assassin.origins.v2ex.core

import com.assassin.origins.v2ex.BuildConfig
import com.assassin.origins.v2ex.api.ApiService
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceManager private constructor() {
    private var apiService: ApiService

    companion object {
        val instance: ApiServiceManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ApiServiceManager()
        }
    }

    init {
        // 初始化 Retrofit
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.addInterceptor(
            LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .build()
        )
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.v2ex.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpBuilder.build())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getApiService(): ApiService {
        return apiService
    }
}