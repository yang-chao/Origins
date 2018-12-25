package com.assassin.origins.splash.api.model

import com.google.gson.annotations.SerializedName

data class Urls(
    @SerializedName("small")
    val small: String = "",
    @SerializedName("thumb")
    val thumb: String = "",
    @SerializedName("raw")
    val raw: String = "",
    @SerializedName("regular")
    val regular: String = "",
    @SerializedName("full")
    val full: String = ""
)