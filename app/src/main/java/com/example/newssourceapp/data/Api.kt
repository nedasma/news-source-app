package com.example.newssourceapp.data

import com.example.newssourceapp.data.model.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "3a8999f331b648b5b49fe03d52823d05"

interface Api {

    @GET("top-headlines")
    suspend fun getAllTopHeadlinesNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<News>
}