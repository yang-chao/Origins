package com.assassin.origins.splash.api.model

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("sponsored_by")
    val sponsoredBy: SponsoredBy,
    @SerializedName("color")
    val color: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("sponsored")
    val sponsored: Boolean = false,
    @SerializedName("sponsored_impressions_id")
    val sponsoredImpressionsId: String = "",
    @SerializedName("liked_by_user")
    val likedByUser: Boolean = false,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("width")
    val width: Int = 0,
    @SerializedName("links")
    val links: Links,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("user")
    val user: User,
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("height")
    val height: Int = 0,
    @SerializedName("likes")
    val likes: Int = 0
)