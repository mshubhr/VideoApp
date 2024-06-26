package com.project.videoapp

import kotlinx.serialization.Serializable

@Serializable
data class videos (
    val id: Int,
    var videourl: String,
    var channelname: String,
    var title: String,
    var description: String,
    var views : Int,
    var likes: Int,
    var comments: Int,
    val thumbnails: String
)