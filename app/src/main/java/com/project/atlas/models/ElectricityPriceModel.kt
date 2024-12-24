package com.project.atlas.models

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("data") val data: Data,
    @SerializedName("included") val included: List<Included>
)

data class Data(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String
)

data class Included(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: Attributes
)

data class Attributes(
    @SerializedName("title") val title: String,
    @SerializedName("values") val values: List<Value>
)

data class Value(
    @SerializedName("value") val price: Double,
    @SerializedName("datetime") val datetime: String
)