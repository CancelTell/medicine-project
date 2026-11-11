package com.example.medicineproject.data

import com.example.medicineproject.domain.Article
import com.example.medicineproject.domain.ArticleRepository

class ArticleRepositoryImpl : ArticleRepository {
    override suspend fun getArticles(): List<Article> {
        return mockArticles
    }

    override suspend fun getArticleById(id: String): Article? {
        return mockArticles.find { it.id == id}
    }
}