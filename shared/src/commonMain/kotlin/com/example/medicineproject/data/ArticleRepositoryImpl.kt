package com.example.medicineproject.data

import com.example.medicineproject.data.datasource.ArticleDataSource
import com.example.medicineproject.data.mappers.toDomain
import com.example.medicineproject.domain.Article
import com.example.medicineproject.domain.ArticlePage
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.domain.errors.LoadException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepositoryImpl(
    private val dataSource: ArticleDataSource,
    private val mappingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ArticleRepository {
    override suspend fun getArticles(query: String, offset: Int, limit: Int): ArticlePage {
        val search = dataSource.search(query.trim().ifBlank { "medicine" }, offset, limit)
        val summaries = dataSource.summaries(search.ids)
        return withContext(mappingDispatcher) {
            val count = search.count.toIntOrNull() ?: throw LoadException.BadResponse()
            val nextOffset = offset + search.ids.size
            ArticlePage(
                items = summaries.map { it.toDomain() },
                // Поиск PubMed отдаёт не более первых 10 000 PMID.
                nextOffset = nextOffset.takeIf { search.ids.isNotEmpty() && it < minOf(count, 10_000) },
            )
        }
    }

    override suspend fun getArticleById(id: String): Article {
        val summary = dataSource.summaries(listOf(id)).singleOrNull() ?: throw LoadException.NotFound()
        val record = dataSource.publicationRecord(id)
        return withContext(mappingDispatcher) { summary.toDomain(record) }
    }

    override suspend fun getRelatedArticles(id: String): List<Article> {
        val ids = dataSource.relatedIds(id).take(10)
        val summaries = dataSource.summaries(ids)
        return withContext(mappingDispatcher) { summaries.map { it.toDomain() } }
    }
}
