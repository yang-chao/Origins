package com.assassin.origins.v2ex.core.repository

import com.assassin.origins.v2ex.api.model.Topic
import com.assassin.origins.v2ex.core.ApiServiceManager
import io.reactivex.Observable

class TopicRepository {

    private val apiService = ApiServiceManager.instance.getApiService()

    fun getTopicHotList(): Observable<List<Topic>> {
        return apiService.getHostList()
    }

}