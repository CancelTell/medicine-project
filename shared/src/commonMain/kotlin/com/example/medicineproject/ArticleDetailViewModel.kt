package com.example.medicineproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArticleDetailState(
    val article: Article? = null
)

class ArticleDetailViewModel(
    private val navigator: Navigator,
    val articleId: String
) : ViewModel() {
    private val _state = MutableStateFlow(ArticleDetailState())
    val state = _state.asStateFlow()

    init {
        val article = mockArticles.find { it.id == articleId}
        _state.value = ArticleDetailState(article = article)
    }
    fun onBack() {
        navigator.back()
    }
}