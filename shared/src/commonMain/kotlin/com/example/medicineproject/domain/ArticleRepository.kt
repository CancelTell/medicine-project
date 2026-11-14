package com.example.medicineproject.domain

interface ArticleRepository {
    suspend fun getArticles(query: String, offset: Int, limit: Int): ArticlePage
    suspend fun getArticleById(id: String): Article
    suspend fun getRelatedArticles(id: String): List<Article>
}
