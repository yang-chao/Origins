package com.assassin.origins.v2ex.api.model

data class Node(
    val footer: String = "",
    val topics: Int = 0,
    val titleAlternative: String = "",
    val stars: Int = 0,
    val avatarNormal: String = "",
    val title: String = "",
    val url: String = "",
    val avatarLarge: String = "",
    val parentNodeName: String = "",
    val avatarMini: String = "",
    val root: Boolean = false,
    val name: String = "",
    val header: String = "",
    val id: Int = 0
)