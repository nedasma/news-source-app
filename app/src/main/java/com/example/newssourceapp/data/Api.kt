package com.example.newssourceapp.data

import com.example.newssourceapp.data.model.News
import com.example.newssourceapp.di.ApplicationModule
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "3a8999f331b648b5b49fe03d52823d05"

/**
 * An interface acting as an entry point for the repositories to query the server endpoints. Currently,
 * this interface contains only one method (AKA endpoint) but it can be easily extended to contain
 * multiple endpoints, provided that it has the same [ApplicationModule]'s base URL.
 */
interface Api {

    /**
     * GETs all top news by the specified two-letter [countryCode]. The [page] parameter allows the
     * queries to be paginated in order to not "bombard" the server with heavy requests. The [apiKey]
     * is needed to have the necessary authorization with the server, so that we can get some data back
     * from the server.
     *
     * Returns a [Response] containing the [News] inside its body.
     */
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