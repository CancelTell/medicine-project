package com.example.medicineproject.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleSearchDto(
    val count: String,
    @SerialName("idlist") val ids: List<String>,
)

@Serializable
data class ArticleSummaryDto(
    @SerialName("uid") val id: String,
    val title: String,
    val authors: List<ArticleAuthorDto> = emptyList(),
    @SerialName("fulljournalname") val journal: String = "",
    @SerialName("pubdate") val publicationDate: String = "",
)

@Serializable
data class ArticleAuthorDto(val name: String)

@Serializable
data class ArticleLinksDto(
    @SerialName("linksets") val linkSets: List<ArticleLinkSetDto>,
)

@Serializable
data class ArticleLinkSetDto(
    @SerialName("linksetdbs") val databases: List<ArticleLinkDatabaseDto> = emptyList(),
)

@Serializable
data class ArticleLinkDatabaseDto(
    @SerialName("linkname") val name: String,
    val links: List<String> = emptyList(),
)
