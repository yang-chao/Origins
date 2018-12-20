package com.assassin.origins.v2ex.api.model

import com.google.gson.annotations.SerializedName

data class Member(
        val website: String = "",
        val github: String = "",
        val created: Int = 0,
        val bio: String = "",
        val psn: String = "",
        @SerializedName("avatar_normal")
        val avatarNormal: String = "",
        val url: String = "",
        val btc: String = "",
        @SerializedName("avatar_large")
        val avatarLarge: String = "",
        val twitter: String = "",
        @SerializedName("avatar_mini")
        val avatarMini: String = "",
        val tagline: String = "",
        val location: String = "",
        val id: Int = 0,
        val username: String = ""
)