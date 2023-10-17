package com.davidgrath.fitnessapp.ui.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.processing.Generated

@Generated("jsonschema2pojo")
data class TempVideoDetails(
    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("author_name")
    @Expose
    var authorName: String? = null,

    @SerializedName("author_url")
    @Expose
    var authorUrl: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("height")
    @Expose
    var height: Int? = null,

    @SerializedName("width")
    @Expose
    var width: Int? = null,

    @SerializedName("version")
    @Expose
    var version: String? = null,

    @SerializedName("provider_name")
    @Expose
    var providerName: String? = null,

    @SerializedName("provider_url")
    @Expose
    var providerUrl: String? = null,

    @SerializedName("thumbnail_height")
    @Expose
    var thumbnailHeight: Int? = null,

    @SerializedName("thumbnail_width")
    @Expose
    var thumbnailWidth: Int? = null,

    @SerializedName("thumbnail_url")
    @Expose
    var thumbnailUrl: String? = null,

    @SerializedName("html")
    @Expose
    var html: String? = null
)