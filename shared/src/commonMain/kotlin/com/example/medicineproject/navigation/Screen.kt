package com.example.medicineproject.navigation

sealed class Screen {
    data object ArticleList : Screen()
    data class ArticleDetail(val articleId: String) : Screen()
}