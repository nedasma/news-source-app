package com.example.newssourceapp.data.repository

import com.example.newssourceapp.data.Api
import com.example.newssourceapp.data.model.News
import com.example.newssourceapp.utils.Result
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * The repository for the News List ViewModel, acting as a data layer in the whole architecture. It's
 * responsible for querying [api]s, databases and/or other data entities existing in the application.
 */
class NewsListRepository @Inject constructor(
    private val api: Api
) {

    /**
     * Acts as an intermediary between the ViewModel and the [api], meaning that in order for the VM
     * to access the data layer, it needs to go through this method. The [page] parameter allows
     * specifying which page of the whole request needs to be returned.
     *
     * Returns a [Result] containing the [News] if the query was successful, and the error with the
     * error message otherwise.
     *
     * NOTE: if the device is offline, this method will throw an [UnknownHostException], however, it'd
     * be wrapped inside the [Result.Error] class, meaning that the app wouldn't crash.
     */
    suspend fun getTopHeadlinesNews(page: Int): Result<News> {
        return try {
            val response = api.getAllTopHeadlinesNews(page = page)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Result.Success(result)
            } else {
                Result.Error(response.message())
            }
        } catch (exception: UnknownHostException) {
            Result.Error(exception.message ?: "Unknown error has occured")
        }
    }
}