package com.example.newssourceapp.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Wrapper data class to contain all [News] returned by the News API. The class contains a list of
 * [articles], a HTTP [status] and the number of [totalResults] returned by the server.
 */
@JsonClass(generateAdapter = true)
data class News(
    @Json(name = "articles")
    val articles: List<Article>,
    @Json(name = "status")
    val status: String,
    @Json(name = "totalResults")
    val totalResults: Int
)