package com.example.medicineproject.domain

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
    suspend fun getArticleById(id : String): Article?
}