package com.example.newssourceapp.data.repository

import com.example.newssourceapp.data.Api
import com.example.newssourceapp.data.model.News
import org.junit.jupiter.api.Test
import com.example.newssourceapp.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import retrofit2.Response
import java.net.UnknownHostException

internal class NewsListRepositoryTest {

    private val api = mockk<Api>()
    private val repository = NewsListRepository(api)

    @Test
    fun `GIVEN request for headlines WHEN response is successful THEN return success`() = runBlocking {
        coEvery { api.getAllTopHeadlinesNews(any(), any(), any()) } returns Response.success(200, mockk<News>())
        val result = repository.getTopHeadlinesNews(1)

        coVerify(exactly = 1) {
            api.getAllTopHeadlinesNews(any(), any(), any())
        }
        assertEquals(Result.Success(result).data, result)
    }

    @Test
    fun `GIVEN request for headlines WHEN response is failed THEN return failure`() = runBlocking {
        coEvery { api.getAllTopHeadlinesNews(any(), any(), any()) } returns Response.error(404, mockk(relaxed = true))
        val result = repository.getTopHeadlinesNews(1)

        coVerify(exactly = 1) {
            api.getAllTopHeadlinesNews(any(), any(), any())
        }
        assertEquals("Response.error()", result.message)
    }

    @Test
    fun `GIVEN request for headlines WHEN there is no internet connection THEN return failure`() = runBlocking {
        coEvery { api.getAllTopHeadlinesNews(any(), any(), any()) } throws UnknownHostException("No internet")
        val result = repository.getTopHeadlinesNews(1)

        coVerify(exactly = 1) {
            api.getAllTopHeadlinesNews(any(), any(), any())
        }
        assertEquals("No internet", result.message)
    }

}
