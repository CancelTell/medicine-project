package com.example.medicineproject.ui.model

import com.example.medicineproject.domain.Article

data class ArticleCardUi(
    val id: String,
    val title: String,
)

data class ArticleDetailUi(
    val title: String,
    val author: String,
    val category: String,
    val description: String,
)

fun Article.toCardUi(): ArticleCardUi = ArticleCardUi(id = id, title = title)

fun Article.toDetailUi(): ArticleDetailUi = ArticleDetailUi(
    title = title,
    author = author,
    category = category,
    description = description,
)