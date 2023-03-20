package com.example.newssourceapp.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class News(
    @Json(name = "articles")
    val articles: List<Article>,
    @Json(name = "status")
    val status: String,
    @Json(name = "totalResults")
    val totalResults: Int
)