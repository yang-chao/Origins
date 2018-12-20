package com.assassin.origins.v2ex.core.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import com.assassin.origins.core.viewmodel.ListDataViewModel
import com.assassin.origins.v2ex.api.model.Topic
import com.assassin.origins.v2ex.core.repository.TopicRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TopicViewModel(application: Application) : ListDataViewModel<Topic>(application) {

    private val topicRepository = TopicRepository()

    @SuppressLint("CheckResult")
    override fun loadData() {
        topicRepository
            .getTopicHotList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data.postValue(it) }, {
                data.postValue(null)
                it.printStackTrace()
            })
    }

}