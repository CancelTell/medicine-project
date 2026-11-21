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

private const val SEARCH_PATH = "esearch.fcgi"
private const val SUMMARY_PATH = "esummary.fcgi"
private const val LINKS_PATH = "elink.fcgi"
private const val FETCH_PATH = "efetch.fcgi"

private const val PARAM_TERM = "term"
private const val PARAM_OFFSET = "retstart"
private const val PARAM_LIMIT = "retmax"
private const val PARAM_ID = "id"
private const val PARAM_COMMAND = "cmd"
private const val PARAM_LINK_NAME = "linkname"
private const val PARAM_RECORD_TYPE = "rettype"
private const val PARAM_DATABASE = "db"
private const val PARAM_RESPONSE_MODE = "retmode"
private const val PARAM_TOOL = "tool"

private const val DATABASE = "pubmed"
private const val JSON_MODE = "json"
private const val TEXT_MODE = "text"
private const val RELATED_COMMAND = "neighbor"
private const val RELATED_LINK_NAME = "pubmed_pubmed"
private const val ABSTRACT_RECORD_TYPE = "abstract"
private const val TOOL_NAME = "MedicineProject"
private const val REQUEST_INTERVAL_MILLIS = 350L

class PubMedApi(
    private val client: HttpClient,
    private val parsingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val requestMutex = Mutex()

    suspend fun search(query: String, offset: Int, limit: Int): ArticleSearchDto {
        val response = request(SEARCH_PATH) {
            parameter(PARAM_TERM, query)
            parameter(PARAM_OFFSET, offset)
            parameter(PARAM_LIMIT, limit)
        }
        return withContext(parsingDispatcher) {
            val result = response.body<JsonObject>().checkError().requiredObject("esearchresult").checkError()
            json.decodeFromJsonElement(result)
        }
    }

    suspend fun summaries(ids: List<String>): List<ArticleSummaryDto> {
        if (ids.isEmpty()) return emptyList()
        val uniqueIds = ids.distinct()
        val response = request(SUMMARY_PATH) {
            parameter(PARAM_ID, uniqueIds.joinToString(","))
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
        val response = request(LINKS_PATH) {
            parameter(PARAM_ID, id)
            parameter(PARAM_COMMAND, RELATED_COMMAND)
            parameter(PARAM_LINK_NAME, RELATED_LINK_NAME)
        }
        return withContext(parsingDispatcher) {
            val body = response.body<JsonObject>().checkError()
            body["linksets"]?.jsonArray?.forEach { it.jsonObject.checkError() }
            val links = json.decodeFromJsonElement<ArticleLinksDto>(body)
            links.linkSets.flatMap { it.databases }
                .filter { it.name == RELATED_LINK_NAME }
                .flatMap { it.links }
                .filter { it != id }
                .distinct()
        }
    }

    suspend fun publicationRecord(id: String): String {
        val response = request(FETCH_PATH, mode = TEXT_MODE) {
            parameter(PARAM_ID, id)
            parameter(PARAM_RECORD_TYPE, ABSTRACT_RECORD_TYPE)
        }
        return withContext(parsingDispatcher) {
            val record = response.bodyAsText().trim()
            if (record.startsWith("Error:", ignoreCase = true)) throw PubMedApiException(record)
            record
        }
    }

    private suspend fun request(
        path: String,
        mode: String = JSON_MODE,
        parameters: HttpRequestBuilder.() -> Unit,
    ): HttpResponse = requestMutex.withLock {
        // Без API-ключа NCBI разрешает не больше трёх запросов в секунду.
        delay(REQUEST_INTERVAL_MILLIS)
        client.get(path) {
            parameter(PARAM_DATABASE, DATABASE)
            parameter(PARAM_RESPONSE_MODE, mode)
            parameter(PARAM_TOOL, TOOL_NAME)
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
