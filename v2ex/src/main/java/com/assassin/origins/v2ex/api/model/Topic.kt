package com.assassin.origins.v2ex.api.model

import com.google.gson.annotations.SerializedName

data class Topic(
    @SerializedName("last_touched")
    val lastTouched: Int = 0,
    val node: Node,
    val replies: Int = 0,
    @SerializedName("last_reply_by")
    val lastReplyBy: String = "",
    val created: Int = 0,
    val member: Member,
    @SerializedName("content_rendered")
    val contentRendered: String = "",
    val id: Int = 0,
    val title: String = "",
    @SerializedName("last_modified")
    val lastModified: Long = 0,
    val url: String = "",
    val content: String = ""
)