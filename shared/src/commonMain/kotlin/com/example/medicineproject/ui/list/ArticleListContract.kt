package com.example.medicineproject.list

import com.example.medicineproject.ui.model.ArticleCardUi

data class ArticleListState(
    val query: String = "",
    val articles: List<ArticleCardUi> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface ArticleListIntent {
    data class QueryChanged(val value: String) : ArticleListIntent
    data class ArticleClicked(val id: String) : ArticleListIntent
}