package com.example.medicineproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArticleListState(
    val articles: List<Article> = mockArticles
)

class ArticleListViewModel(
    private val navigator: Navigator
) : ViewModel() {
    private val _state = MutableStateFlow(ArticleListState())
    val state = _state.asStateFlow()

    fun onArticleClick(article: Article){
        navigator.addToBackStack(Screen.ArticleDetail(article.id))
    }
}