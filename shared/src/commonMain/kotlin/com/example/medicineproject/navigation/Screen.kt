package com.example.medicineproject

sealed interface Screen {
    data object ArticleList : Screen
    data class ArticleDetail(val id: String) : Screen
}