package com.example.medicineproject.data.network

import com.example.medicineproject.data.api.PubMedApi
import com.example.medicineproject.data.api.PubMedApiException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PubMedApiTest {
    @Test
    fun searchPassesQueryAndPaginationAndIgnoresUnknownFields() = runTest {
        withApi(
            response = """{"header":{},"esearchresult":{"count":"2","idlist":["24102982","21171099"],"retstart":"20"}}""",
            checkRequest = { request ->
                assertEquals("/entrez/eutils/esearch.fcgi", request.url.encodedPath)
                assertEquals("pubmed", request.url.parameters["db"])
                assertEquals("json", request.url.parameters["retmode"])
                assertEquals("asthma & children", request.url.parameters["term"])
                assertEquals("20", request.url.parameters["retstart"])
                assertEquals("2", request.url.parameters["retmax"])
            },
        ) { api ->
            val result = api.search("asthma & children", offset = 20, limit = 2)
            assertEquals("2", result.count)
            assertEquals(listOf("24102982", "21171099"), result.ids)
        }
    }

    @Test
    fun summariesKeepSearchOrderAndReadRecordsByPmid() = runTest {
        withApi(
            response = """
                {"result":{
                    "uids":["21171099","24102982"],
                    "21171099":{"uid":"21171099","title":"Second article"},
                    "24102982":{"uid":"24102982","title":"First article",
                        "authors":[{"name":"Wu MP","authtype":"Author"}],
                        "fulljournalname":"The FEBS journal","pubdate":"2013 Dec"}
                }}
            """.trimIndent(),
            checkRequest = { request ->
                assertEquals("24102982,21171099", request.url.parameters["id"])
            },
        ) { api ->
            val articles = api.summaries(listOf("24102982", "21171099", "24102982"))
            assertEquals(listOf("24102982", "21171099"), articles.map { it.id })
            assertEquals("Wu MP", articles.first().authors.single().name)
            assertEquals("The FEBS journal", articles.first().journal)
            assertEquals("2013 Dec", articles.first().publicationDate)
            assertEquals(emptyList(), articles.last().authors)
        }
    }

    @Test
    fun relatedIdsExcludeOriginalArticleDuplicatesAndOtherLinkTypes() = runTest {
        withApi(
            response = """
                {"linksets":[{"ids":["24102982"],"linksetdbs":[
                    {"linkname":"pubmed_pubmed","links":["24102982","21936881","21936881","21346196"]},
                    {"linkname":"pubmed_pmc","links":["3877849"]}
                ]}]}
            """.trimIndent(),
            checkRequest = { request ->
                assertEquals("pubmed_pubmed", request.url.parameters["linkname"])
            },
        ) { api ->
            assertEquals(listOf("21936881", "21346196"), api.relatedIds("24102982"))
        }
    }

    @Test
    fun emptySummaryListDoesNotSendRequest() = runTest {
        withApi("{}", checkRequest = { error("Unexpected request") }) { api ->
            assertEquals(emptyList(), api.summaries(emptyList()))
        }
    }

    @Test
    fun backendErrorInsideSuccessfulResponseIsNotEmptySearch() = runTest {
        withApi("""{"esearchresult":{"ERROR":"Search Backend failed"}}""") { api ->
            val error = assertFailsWith<PubMedApiException> {
                api.search("asthma", offset = 0, limit = 20)
            }
            assertEquals("PubMed: \"Search Backend failed\"", error.message)
        }
    }

    @Test
    fun missingSummaryIsNotSilentlyDropped() = runTest {
        withApi("""{"result":{"uids":[]}}""") { api ->
            assertFailsWith<SerializationException> { api.summaries(listOf("24102982")) }
        }
    }

    @Test
    fun malformedSearchIsNotEmptySearch() = runTest {
        withApi("""{"esearchresult":{"count":"2"}}""") { api ->
            assertFailsWith<SerializationException> {
                api.search("asthma", offset = 0, limit = 20)
            }
        }
    }

    @Test
    fun cancellationIsPropagated() = runTest {
        withApi("{}", checkRequest = { throw CancellationException("Cancelled request") }) { api ->
            assertFailsWith<CancellationException> {
                api.search("asthma", offset = 0, limit = 20)
            }
        }
    }

    @Test
    fun linkSetErrorIsNotAnEmptyRelatedList() = runTest {
        withApi("""{"linksets":[{"ERROR":"Backend failed"}]}""") { api ->
            assertFailsWith<PubMedApiException> { api.relatedIds("24102982") }
        }
    }

    @Test
    fun publicationRecordUsesTextAbstractFormat() = runTest {
        withApi(
            response = "  Citation\n\nAbstract text.  ",
            checkRequest = { request ->
                assertEquals("/entrez/eutils/efetch.fcgi", request.url.encodedPath)
                assertEquals("text", request.url.parameters["retmode"])
                assertEquals("abstract", request.url.parameters["rettype"])
                assertEquals("24102982", request.url.parameters["id"])
            },
        ) { api ->
            assertEquals("Citation\n\nAbstract text.", api.publicationRecord("24102982"))
        }
    }

    private suspend fun withApi(
        response: String,
        checkRequest: (HttpRequestData) -> Unit = {},
        block: suspend (PubMedApi) -> Unit,
    ) {
        val client = HttpClient(MockEngine { request ->
            checkRequest(request)
            respond(response, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }) {
            expectSuccess = true
            defaultRequest { url("https://example.test/entrez/eutils/") }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        try {
            block(PubMedApi(client))
        } finally {
            client.close()
        }
    }
}
