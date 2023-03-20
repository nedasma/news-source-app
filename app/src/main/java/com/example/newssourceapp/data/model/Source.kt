package com.example.newssourceapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the [Source] of the [Article]. It has the source [id] and the [name] of the
 * source (usually, that is the article publisher).
 */
@JsonClass(generateAdapter = true)
data class Source(
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String
)
