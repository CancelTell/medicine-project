package com.example.medicineproject

import com.example.medicineproject.domain.Article
import com.example.medicineproject.domain.ArticlePage
import com.example.medicineproject.domain.ArticleRepository

internal class TestArticleRepository : ArticleRepository {
    var pages: suspend (String, Int, Int) -> ArticlePage = { _, _, _ -> ArticlePage(emptyList(), null) }
    var detail: suspend (String) -> Article = { article(it) }
    var related: suspend (String) -> List<Article> = { emptyList() }
    override suspend fun getArticles(query: String, offset: Int, limit: Int) = pages(query, offset, limit)
    override suspend fun getArticleById(id: String) = detail(id)
    override suspend fun getRelatedArticles(id: String) = related(id)
}

internal fun article(id: String) = Article(id, "Article $id", "Record $id", "Author", "Journal", "2026")
