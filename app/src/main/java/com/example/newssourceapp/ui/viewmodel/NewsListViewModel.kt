package com.example.newssourceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newssourceapp.data.model.Article
import com.example.newssourceapp.data.repository.NewsListRepository
import com.example.newssourceapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repository: NewsListRepository
) : ViewModel() {

    sealed class UiState {
        class Success(val data: List<Article>) : UiState()
        class Error(val error: String) : UiState()
        object Loading : UiState()
    }


    private val _data = MutableStateFlow<UiState>(UiState.Loading)
    val data: StateFlow<UiState> = _data

    private var page = 1
    private var articleList: MutableList<Article> = mutableListOf()

    fun load(setIncrementer: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (setIncrementer) {
                page++
            }
            when (val response = repository.getTopHeadlinesNews(page)) {
                is Result.Success -> {
                    val articles = response.data?.articles ?: emptyList()
                    val formattedArticles = articles.map {
                        val formattedTime = if (it.publishedAt.isNotEmpty()) {
                            ZonedDateTime
                                .parse(it.publishedAt)
                                .format(DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm"))
                        } else {
                            it.publishedAt
                        }
                        it.copy(publishedAt = formattedTime)
                    }
                    if (articleList.isEmpty()) {
                        articleList.addAll(formattedArticles)
                        _data.value = UiState.Success(formattedArticles)
                    } else {
                        val existingArticles = articleList
                        existingArticles.addAll(formattedArticles)
                        _data.value = UiState.Success(articleList)
                    }
                }
                else -> {
                    _data.value = UiState.Error(response.message ?: "Unknown error occured")
                }
            }
        }
    }
}