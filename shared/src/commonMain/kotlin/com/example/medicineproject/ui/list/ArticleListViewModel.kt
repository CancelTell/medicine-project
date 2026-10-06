package com.example.medicineproject.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineproject.Screen
import com.example.medicineproject.domain.Article
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.ui.model.toCardUi
import com.example.medicineproject.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val navigator: Navigator,
    private val repository: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ArticleListState())
    val state: StateFlow<ArticleListState> = _state.asStateFlow()

    private var allArticles: List<Article> = emptyList()

    init {
        viewModelScope.launch {
            allArticles = repository.getArticles()
            _state.update { it.copy(articles = filter(it.query), isLoading = false) }
        }
    }

    fun onIntent(intent: ArticleListIntent) {
        when (intent) {
            is ArticleListIntent.ArticleClicked -> navigator.open(Screen.ArticleDetail(intent.id))
            is ArticleListIntent.QueryChanged -> _state.update {
                it.copy(query = intent.value, articles = filter(intent.value))
            }
        }
    }

    private fun filter(query: String) =
        allArticles
            .filter { it.title.contains(query.trim(), ignoreCase = true) }
            .map { it.toCardUi() }
}