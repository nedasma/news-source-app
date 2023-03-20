package com.example.newssourceapp.ui.viewmodel

import app.cash.turbine.test
import com.example.newssourceapp.data.model.Article
import com.example.newssourceapp.data.model.News
import com.example.newssourceapp.data.model.Source
import com.example.newssourceapp.data.repository.NewsListRepository
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel.UiState
import com.example.newssourceapp.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NewsListViewModelTest {

    private val repository = mockk<NewsListRepository>()
    private val testArticles = listOf(
        Article(
            author = null,
            content = null,
            description = "Test desc 1",
            publishedAt = "2023-03-10T14:14:33Z",
            source = Source(id = null, name = ""),
            title = "Some test title 1",
            url = "https://www.google.com/",
            urlToImage = null
        ),
        Article(
            author = null,
            content = null,
            description = "Test desc 2",
            publishedAt = "2023-03-10T23:57:59Z",
            source = Source(id = null, name = ""),
            title = "Some test title 2",
            url = "https://www.bing.com/",
            urlToImage = null,
        )
    )
    private val testNews = News(
        articles = testArticles,
        status = "status",
        totalResults = 2,
    )
    private val viewModel = NewsListViewModel(repository)

    @BeforeEach
    fun setup() {
        coEvery { repository.getTopHeadlinesNews(any()) } returns Result.Success(testNews)
    }

    @Test
    fun `GIVEN viewmodel and repository WHEN calling for loading of data THEN return the data`() = runBlocking {
        viewModel.load(false)
        coVerify (exactly = 1) {
            repository.getTopHeadlinesNews(any())
        }

        viewModel.data.test {
            assertEquals(UiState.Success(testArticles), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN repository returning failure WHEN calling for loading of data THEN handle the errors`() = runBlocking {
        val errorState = UiState.Error("error")
        coEvery { repository.getTopHeadlinesNews(any()) } returns Result.Error("error")

        viewModel.load(false)
        viewModel.data.test {
            assertEquals(errorState, awaitItem())
            awaitComplete()
        }
    }
}