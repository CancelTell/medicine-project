package com.example.medicineproject.domain

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
    suspend fun getArticleById(id : String): Article?
}

suspend fun ArticleRepository.getRelatedArticles(article: Article): List<Article> =
    getArticles().filter {
        it.id != article.id && (it.author == article.author || it.category == article.category)
    }