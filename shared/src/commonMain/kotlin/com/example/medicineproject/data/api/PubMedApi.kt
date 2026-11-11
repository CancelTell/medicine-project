package com.example.medicineproject.data.api

import com.example.medicineproject.data.dto.ArticleLinksDto
import com.example.medicineproject.data.dto.ArticleSearchDto
import com.example.medicineproject.data.dto.ArticleSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray

class PubMedApi(
    private val client: HttpClient,
    private val parsingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val requestMutex = Mutex()

    suspend fun search(query: String, offset: Int, limit: Int): ArticleSearchDto {
        val response = request("esearch.fcgi") {
            parameter("term", query)
            parameter("retstart", offset)
            parameter("retmax", limit)
        }
        return withContext(parsingDispatcher) {
            val result = response.body<JsonObject>().checkError().requiredObject("esearchresult").checkError()
            json.decodeFromJsonElement(result)
        }
    }

    suspend fun summaries(ids: List<String>): List<ArticleSummaryDto> {
        if (ids.isEmpty()) return emptyList()
        val uniqueIds = ids.distinct()
        val response = request("esummary.fcgi") {
            parameter("id", uniqueIds.joinToString(","))
        }
        return withContext(parsingDispatcher) {
            val result = response.body<JsonObject>().checkError().requiredObject("result").checkError()
            // Порядок ключей JSON не обязан совпадать с порядком результатов поиска.
            uniqueIds.map { id ->
                json.decodeFromJsonElement<ArticleSummaryDto>(result.requiredObject(id).checkError())
            }
        }
    }

    suspend fun relatedIds(id: String): List<String> {
        val response = request("elink.fcgi") {
            parameter("id", id)
            parameter("cmd", "neighbor")
            parameter("linkname", "pubmed_pubmed")
        }
        return withContext(parsingDispatcher) {
            val body = response.body<JsonObject>().checkError()
            body["linksets"]?.jsonArray?.forEach { it.jsonObject.checkError() }
            val links = json.decodeFromJsonElement<ArticleLinksDto>(body)
            links.linkSets.flatMap { it.databases }
                .filter { it.name == "pubmed_pubmed" }
                .flatMap { it.links }
                .filter { it != id }
                .distinct()
        }
    }

    suspend fun publicationRecord(id: String): String {
        val response = request("efetch.fcgi", mode = "text") {
            parameter("id", id)
            parameter("rettype", "abstract")
        }
        return withContext(parsingDispatcher) {
            val record = response.bodyAsText().trim()
            if (record.startsWith("Error:", ignoreCase = true)) throw PubMedApiException(record)
            record
        }
    }

    private suspend fun request(
        path: String,
        mode: String = "json",
        parameters: HttpRequestBuilder.() -> Unit,
    ): HttpResponse = requestMutex.withLock {
        // Без API-ключа NCBI разрешает не больше трёх запросов в секунду.
        delay(350)
        client.get(path) {
            parameter("db", "pubmed")
            parameter("retmode", mode)
            parameter("tool", "MedicineProject")
            parameters()
        }
    }

    private fun JsonObject.requiredObject(name: String): JsonObject =
        this[name]?.jsonObject ?: throw SerializationException("Missing PubMed field: $name")

    private fun JsonObject.checkError(): JsonObject {
        // E-utilities может вернуть ошибку в JSON даже при HTTP 200.
        val error = this["error"] ?: this["ERROR"]
        if (error != null) throw PubMedApiException("PubMed: $error")
        return this
    }
}

class PubMedApiException(message: String) : Exception(message)
