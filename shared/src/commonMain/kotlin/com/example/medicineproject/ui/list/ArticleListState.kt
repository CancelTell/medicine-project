package com.example.medicineproject.ui.list

import com.example.medicineproject.domain.Article
import com.example.medicineproject.ui.model.ArticleCardUi

data class ArticleListState(
    val query: String = "",
    val articles: List<ArticleCardUi> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface ArticleListIntent {
    data class QueryChanged(val value: String) : ArticleListIntent
    data class ArticleClicked(val id: Article) : ArticleListIntent
}