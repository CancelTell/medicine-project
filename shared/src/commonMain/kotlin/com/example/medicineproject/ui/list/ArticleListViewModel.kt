package com.example.medicineproject.ui.list

import androidx.lifecycle.ViewModel
import com.example.medicineproject.data.mockArticles
import com.example.medicineproject.domain.Article
import com.example.medicineproject.navigation.Navigator
import com.example.medicineproject.navigation.Screen
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
        navigator.open(Screen.ArticleDetail(article.id))
    }
}