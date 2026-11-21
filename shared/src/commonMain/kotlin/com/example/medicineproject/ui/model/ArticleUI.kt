package com.example.medicineproject.ui.model

import androidx.compose.runtime.Immutable
import com.example.medicineproject.domain.Article

@Immutable
data class ArticleCardUi(val id: String, val title: String)

@Immutable
data class ArticleDetailUi(
    val title: String,
    val author: String,
    val journal: String,
    val publicationDate: String,
    val description: String,
    val related: List<ArticleCardUi> = emptyList(),
)

fun Article.toCardUi(): ArticleCardUi = ArticleCardUi(id = id, title = title)

fun Article.toDetailUi(): ArticleDetailUi = ArticleDetailUi(
    title = title,
    author = author,
    journal = journal,
    publicationDate = publicationDate,
    description = description,
)
