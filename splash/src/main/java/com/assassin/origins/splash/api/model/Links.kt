package com.assassin.origins.splash.api.model

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("followers")
    val followers: String = "",
    @SerializedName("portfolio")
    val portfolio: String = "",
    @SerializedName("following")
    val following: String = "",
    @SerializedName("self")
    val self: String = "",
    @SerializedName("html")
    val html: String = "",
    @SerializedName("photos")
    val photos: String = "",
    @SerializedName("likes")
    val likes: String = ""
)