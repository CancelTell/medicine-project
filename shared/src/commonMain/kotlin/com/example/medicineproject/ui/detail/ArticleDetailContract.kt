package com.example.medicineproject.detail

import com.example.medicineproject.ui.model.ArticleDetailUi

data class ArticleDetailState(
    val article: ArticleDetailUi? = null,
    val isLoading: Boolean = true,
)

sealed interface ArticleDetailIntent {
    data object BackClicked : ArticleDetailIntent
}