package com.assassin.origins.splash.api.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("total_photos")
    val totalPhotos: Int = 0,
    @SerializedName("accepted_tos")
    val acceptedTos: Boolean = false,
    @SerializedName("twitter_username")
    val twitterUsername: String = "",
    @SerializedName("last_name")
    val lastName: String = "",
    @SerializedName("bio")
    val bio: String = "",
    @SerializedName("total_likes")
    val totalLikes: Int = 0,
    @SerializedName("portfolio_url")
    val portfolioUrl: String = "",
    @SerializedName("profile_image")
    val profileImage: ProfileImage,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("links")
    val links: Links,
    @SerializedName("total_collections")
    val totalCollections: Int = 0,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("instagram_username")
    val instagramUsername: String = "",
    @SerializedName("username")
    val username: String = ""
)