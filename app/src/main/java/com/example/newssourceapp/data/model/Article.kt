package com.example.newssourceapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Data class which represents the [Article]s given by the News API in JSON format. The [author]
 * denotes the author(s) of the article (can be none - in that case it's saved as null), [content] contains
 * the actual article, the [title] is the article title, while the [description] acts as a short
 * summary for the article. In order to get the publication date, one can refer to the [publishedAt]
 * parameter, and the [source] can be found in the separate data class.
 * The leading image of the article is stored as a [urlToImage], while the article URL itself can be
 * accessed via the [url] parameter.
 */
@JsonClass(generateAdapter = true)
data class Article(
    @Json(name = "author")
    val author: String?,
    @Json(name = "content")
    val content: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "publishedAt")
    val publishedAt: String,
    @Json(name = "source")
    val source: Source,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String,
    @Json(name = "urlToImage")
    val urlToImage: String?
) : Serializable
