package com.example.medicineproject.data

import com.example.medicineproject.data.datasource.ArticleDataSource
import com.example.medicineproject.data.dto.ArticleAuthorDto
import com.example.medicineproject.data.dto.ArticleSearchDto
import com.example.medicineproject.data.dto.ArticleSummaryDto
import com.example.medicineproject.domain.errors.LoadException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ArticleRepositoryTest {
    @Test
    fun emptySearchDoesNotHaveANextPage() = runTest {
        val repository = ArticleRepositoryImpl(TestDataSource())
        val page = repository.getArticles("missing", 0, 20)
        assertEquals(emptyList(), page.items)
        assertNull(page.nextOffset)
    }

    @Test
    fun pageOffsetUsesServerIdsAndDetailContainsRealRecord() = runTest {
        val source = TestDataSource().apply {
            result = ArticleSearchDto("30", listOf("1", "2"))
        }
        val repository = ArticleRepositoryImpl(source)
        val page = repository.getArticles("medicine", 0, 20)
        assertEquals(2, page.nextOffset)
        val article = repository.getArticleById("1")
        assertEquals("PubMed record 1", article.description)
        assertEquals("First author, Second author", article.author)
        assertEquals("Journal", article.journal)
    }

    @Test
    fun summaryFailureDoesNotBecomeAnEmptyPage() = runTest {
        val source = TestDataSource().apply {
            result = ArticleSearchDto("30", listOf("1"))
            failSummaries = true
        }
        assertFailsWith<LoadException.NoConnection> {
            ArticleRepositoryImpl(source).getArticles("medicine", 0, 20)
        }
    }

    @Test
    fun relatedPublicationSummariesAreLoadedInOneBatch() = runTest {
        val source = TestDataSource()
        val articles = ArticleRepositoryImpl(source).getRelatedArticles("1")
        assertEquals(listOf("2", "3"), source.lastSummaryIds)
        assertEquals(listOf("2", "3"), articles.map { it.id })
    }

    private class TestDataSource : ArticleDataSource {
        var result = ArticleSearchDto("0", emptyList())
        var failSummaries = false
        var lastSummaryIds = emptyList<String>()
        override suspend fun search(query: String, offset: Int, limit: Int) = result
        override suspend fun summaries(ids: List<String>): List<ArticleSummaryDto> {
            lastSummaryIds = ids
            if (failSummaries) throw LoadException.NoConnection()
            return ids.map {
                ArticleSummaryDto(it, "Article $it", listOf(ArticleAuthorDto("First author"), ArticleAuthorDto("Second author")), "Journal", "2026")
            }
        }
        override suspend fun relatedIds(id: String) = listOf("2", "3")
        override suspend fun publicationRecord(id: String) = "PubMed record $id"
    }
}
