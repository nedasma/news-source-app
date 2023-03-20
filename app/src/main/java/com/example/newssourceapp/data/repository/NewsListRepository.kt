package com.example.newssourceapp.data.repository

import com.example.newssourceapp.data.Api
import com.example.newssourceapp.data.model.News
import com.example.newssourceapp.utils.Result
import java.net.UnknownHostException
import javax.inject.Inject

class NewsListRepository @Inject constructor(
    private val api: Api
) {

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