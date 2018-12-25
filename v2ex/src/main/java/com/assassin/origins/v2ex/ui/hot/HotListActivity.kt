package com.assassin.origins.v2ex.ui.hot

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.assassin.origins.core.viewmodel.ListDataViewModel
import com.assassin.origins.ui.base.BaseApplication
import com.assassin.origins.ui.request.list.RequestListActivity
import com.assassin.origins.ui.request.list.ViewBinder
import com.assassin.origins.v2ex.R
import com.assassin.origins.v2ex.api.model.Topic
import com.assassin.origins.v2ex.core.viewmodel.TopicViewModel
import com.assassin.origins.v2ex.databinding.AdapterTopicBinding
import com.bumptech.glide.Glide

class HotListActivity : RequestListActivity<Topic>() {

    override fun createViewModel(): ListDataViewModel<Topic> {
        return TopicViewModel(BaseApplication.instance)
    }

    override fun createViewBinder(): ViewBinder<Topic> {
        return TopicBinder(this)
    }

    class TopicBinder(val context: Context) : ViewBinder<Topic>() {
        private val layoutInflater = LayoutInflater.from(context)

        override fun createViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = DataBindingUtil.inflate<AdapterTopicBinding>(
                layoutInflater,
                R.layout.adapter_topic, parent, false
            )
            return TopicHolder(binding)
        }

        override fun bindView(holder: RecyclerView.ViewHolder, data: Topic, position: Int) {
            val topicHolder: TopicHolder = holder as TopicHolder
            Glide.with(context)
                .load("http:${data.member.avatarNormal.replace("_mini", "_large")}")
                .into(topicHolder.avatarView)
            topicHolder.dataBinding.topic = data
            topicHolder.dataBinding.executePendingBindings()
        }

        inner class TopicHolder(val dataBinding: AdapterTopicBinding) : RecyclerView.ViewHolder(dataBinding.root) {
            val avatarView = dataBinding.root.findViewById<ImageView>(R.id.avatar)!!

            init {
                dataBinding.root.setOnClickListener {

                }
            }
        }
    }
}