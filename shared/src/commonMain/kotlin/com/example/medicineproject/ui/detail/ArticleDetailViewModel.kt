package com.example.medicineproject.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.domain.errors.LoadException
import com.example.medicineproject.navigation.Navigator
import com.example.medicineproject.navigation.Screen
import com.example.medicineproject.ui.model.toCardUi
import com.example.medicineproject.ui.model.toDetailUi
import com.example.medicineproject.ui.model.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleDetailViewModel(
    private val articleId: String,
    private val navigator: Navigator,
    private val repository: ArticleRepository,
    private val mappingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _state = MutableStateFlow<ArticleDetailState>(ArticleDetailState.Loading)
    val state: StateFlow<ArticleDetailState> = _state.asStateFlow()
    private var loadJob: Job? = null
    private var relatedJob: Job? = null

    init {
        load()
    }

    fun onIntent(intent: ArticleDetailIntent) {
        when (intent) {
            ArticleDetailIntent.BackClicked -> {
                loadJob?.cancel()
                relatedJob?.cancel()
                navigator.back()
            }
            is ArticleDetailIntent.RelatedClicked -> navigator.open(Screen.ArticleDetail(intent.id))
            ArticleDetailIntent.Retry -> load()
            ArticleDetailIntent.RetryRelated -> loadRelated()
        }
    }

    private fun load() {
        loadJob?.cancel()
        relatedJob?.cancel()
        _state.value = ArticleDetailState.Loading
        loadJob = viewModelScope.launch {
            _state.value = try {
                val article = repository.getArticleById(articleId)
                val ui = withContext(mappingDispatcher) { article.toDetailUi() }
                ArticleDetailState.Success(ui)
            } catch (_: LoadException.NotFound) {
                ArticleDetailState.NotFound
            } catch (error: LoadException) {
                ArticleDetailState.Error(error.toUi())
            }
            if (_state.value is ArticleDetailState.Success) loadRelated()
        }
    }

    private fun loadRelated() {
        val current = _state.value as? ArticleDetailState.Success ?: return
        relatedJob?.cancel()
        _state.value = current.copy(isLoadingRelated = true, relatedError = null)
        relatedJob = viewModelScope.launch {
            val content = try {
                val articles = repository.getRelatedArticles(articleId)
                val cards = withContext(mappingDispatcher) { articles.map { it.toCardUi() } }
                current.copy(article = current.article.copy(related = cards), isLoadingRelated = false, relatedError = null)
            } catch (error: LoadException) {
                current.copy(isLoadingRelated = false, relatedError = error.toUi())
            }
            _state.update { content }
        }
    }
}
