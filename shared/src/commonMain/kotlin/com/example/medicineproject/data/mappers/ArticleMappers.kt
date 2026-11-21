package com.example.medicineproject.data.mappers

import com.example.medicineproject.data.dto.ArticleSummaryDto
import com.example.medicineproject.domain.Article

fun ArticleSummaryDto.toDomain(description: String = ""): Article = Article(
    id = id,
    title = title,
    description = description,
    author = authors.joinToString(", ") { it.name },
    journal = journal,
    publicationDate = publicationDate,
)
