package com.example.medicineproject.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineproject.Screen
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.domain.getRelatedArticles
import com.example.medicineproject.ui.model.toDetailUi
import com.example.medicineproject.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val articleId: String,
    private val navigator: Navigator,
    private val repository: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ArticleDetailState())
    val state: StateFlow<ArticleDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val article = repository.getArticleById(articleId)
            val related = article?.let {repository.getRelatedArticles(it)}.orEmpty()
            _state.value = ArticleDetailState(article = article?.toDetailUi(related), isLoading = false)
        }
    }

    fun onIntent(intent: ArticleDetailIntent) {
        when (intent) {
            ArticleDetailIntent.BackClicked -> navigator.back()
            is ArticleDetailIntent.RelatedClicked -> navigator.open(Screen.ArticleDetail(intent.id))
        }
    }
}