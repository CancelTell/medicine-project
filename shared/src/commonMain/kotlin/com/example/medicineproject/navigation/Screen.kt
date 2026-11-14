package com.example.medicineproject.navigation

sealed interface Screen {
    data object ArticleList : Screen
    data class ArticleDetail(val id: String, val entryId: Long = 0) : Screen
}
