package com.assassin.origins.splash.api.model

import com.google.gson.annotations.SerializedName

data class ProfileImage(
    @SerializedName("small")
    val small: String = "",
    @SerializedName("large")
    val large: String = "",
    @SerializedName("medium")
    val medium: String = ""
)