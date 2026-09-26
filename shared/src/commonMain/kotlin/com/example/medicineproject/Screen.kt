package com.example.medicineproject

sealed class Screen {
    data object ArticleList : Screen()
    data class ArticleDetail(val articleId: String) : Screen()
}