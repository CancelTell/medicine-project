package com.example.medicineproject.data.datasource

import com.example.medicineproject.data.dto.ArticleSearchDto
import com.example.medicineproject.data.dto.ArticleSummaryDto

interface ArticleDataSource {
    suspend fun search(query: String, offset: Int, limit: Int): ArticleSearchDto
    suspend fun summaries(ids: List<String>): List<ArticleSummaryDto>
    suspend fun relatedIds(id: String): List<String>
    suspend fun publicationRecord(id: String): String
}
