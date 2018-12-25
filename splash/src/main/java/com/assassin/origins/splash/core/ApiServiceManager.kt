package com.assassin.origins.splash.core

import com.assassin.origins.splash.BuildConfig
import com.assassin.origins.splash.api.ApiService
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
        httpBuilder.addInterceptor {
            val request = it.request().newBuilder()
                .header("Authorization", "Client-ID e0e3322212378f5312de1591a2a406700c17834c9935012ea2111962e3f2a791")
                .header("Accept-Version", "v1")
                .build()
            return@addInterceptor it.proceed(request)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com")
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