package com.assassin.origins.v2ex.api.model

data class Topic(
    val lastTouched: Int = 0,
    val node: Node,
    val replies: Int = 0,
    val lastReplyBy: String = "",
    val created: Int = 0,
    val member: Member,
    val contentRendered: String = "",
    val id: Int = 0,
    val title: String = "",
    val lastModified: Int = 0,
    val url: String = "",
    val content: String = ""
)