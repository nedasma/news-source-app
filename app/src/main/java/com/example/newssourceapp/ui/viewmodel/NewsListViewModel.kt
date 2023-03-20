package com.example.newssourceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newssourceapp.data.model.Article
import com.example.newssourceapp.data.repository.NewsListRepository
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel.UiState
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel.UiState.Error
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel.UiState.Loading
import com.example.newssourceapp.ui.viewmodel.NewsListViewModel.UiState.Success
import com.example.newssourceapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * The ViewModel (or in a shortened form, VM) is responsible for the "business logic" of the specific
 * functionality in question. In this case, it gets the data from the [repository] (which is injected
 * through the constructor), formats it, puts it into a [StateFlow] containing the [UiState] which is
 * then being collected on the UI side.
 */
@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repository: NewsListRepository
) : ViewModel() {

    /**
     * Helper sealed class to store the UI state. It has three statuses:
     * - [Success] to denote the successful data loading operation
     * - [Error] to denote some kind of failure when loading the data from the [repository]
     * - [Loading] to denote a starting status of the [MutableStateFlow].
     *
     * In a more realistic scenario with more UI states, the [Loading] parameter could be useful for
     * showing "not yet ready" data to the user - the most common example would be a spinner.
     */
    sealed class UiState {
        class Success(val data: List<Article>) : UiState()
        class Error(val error: String) : UiState()
        object Loading : UiState()
    }


    private val _data = MutableStateFlow<UiState>(UiState.Loading)
    val data: StateFlow<UiState> = _data

    private var page = 1
    private var articleList: MutableList<Article> = mutableListOf()

    /**
     * Loads the data from the [repository], formats it and passes it to the [data] flow. The [setIncrementer]
     * flag is needed to track the pagination status (whether the pagination isn't needed i.e. the query
     * asks for the 1st page of the data or there are other pages needed to be loaded).
     */
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