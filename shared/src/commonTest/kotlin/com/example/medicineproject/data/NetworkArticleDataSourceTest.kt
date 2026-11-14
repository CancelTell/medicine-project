package com.example.medicineproject.data

import com.example.medicineproject.data.api.PubMedApi
import com.example.medicineproject.data.datasource.NetworkArticleDataSource
import com.example.medicineproject.domain.errors.LoadException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NetworkArticleDataSourceTest {
    @Test
    fun httpServerFailureIsDomainServerError() = runTest {
        withSource(status = HttpStatusCode.ServiceUnavailable) { source ->
            assertFailsWith<LoadException.Server> { source.search("medicine", 0, 20) }
        }
    }

    @Test
    fun invalidRequestIsDifferentFromServerFailure() = runTest {
        withSource(status = HttpStatusCode.BadRequest) { source ->
            assertFailsWith<LoadException.Request> { source.search("medicine", 0, 20) }
        }
    }

    @Test
    fun backendErrorWithHttp200IsDomainServerError() = runTest {
        withSource(body = """{"esearchresult":{"ERROR":"Search Backend failed"}}""") { source ->
            assertFailsWith<LoadException.Server> { source.search("medicine", 0, 20) }
        }
    }

    @Test
    fun malformedJsonIsBadResponse() = runTest {
        withSource(body = "not json") { source ->
            assertFailsWith<LoadException.BadResponse> { source.search("medicine", 0, 20) }
        }
    }

    @Test
    fun cancellationIsNotConvertedToConnectionFailure() = runTest {
        withSource(failure = CancellationException("Screen closed")) { source ->
            assertFailsWith<CancellationException> { source.search("medicine", 0, 20) }
        }
    }

    @Test
    fun browserFailureThatIsNotAnExceptionIsConnectionFailure() = runTest {
        withSource(failure = Error("Fail to fetch")) { source ->
            assertFailsWith<LoadException.NoConnection> { source.search("medicine", 0, 20) }
        }
    }

    @Test
    fun unresolvedHostIsConnectionFailureRatherThanBadResponse() = runTest {
        withSource(failure = UnresolvedAddressException()) { source ->
            assertFailsWith<LoadException.NoConnection> { source.search("medicine", 0, 20) }
        }
    }

    private suspend fun withSource(
        status: HttpStatusCode = HttpStatusCode.OK,
        body: String = "{}",
        failure: Throwable? = null,
        block: suspend (NetworkArticleDataSource) -> Unit,
    ) {
        val client = HttpClient(MockEngine {
            failure?.let { throw it }
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }) {
            expectSuccess = true
            defaultRequest { url("https://example.test/") }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        try { block(NetworkArticleDataSource(PubMedApi(client))) } finally { client.close() }
    }
}
