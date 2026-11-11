package com.example.medicineproject.data.datasource

import com.example.medicineproject.data.api.PubMedApi
import com.example.medicineproject.data.api.PubMedApiException
import com.example.medicineproject.data.dto.ArticleSearchDto
import com.example.medicineproject.data.dto.ArticleSummaryDto
import com.example.medicineproject.domain.errors.LoadException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

class NetworkArticleDataSource(private val api: PubMedApi) : ArticleDataSource {
    override suspend fun search(query: String, offset: Int, limit: Int): ArticleSearchDto =
        guard { api.search(query, offset, limit) }

    override suspend fun summaries(ids: List<String>): List<ArticleSummaryDto> = guard { api.summaries(ids) }
    override suspend fun relatedIds(id: String): List<String> = guard { api.relatedIds(id) }
    override suspend fun publicationRecord(id: String): String = guard { api.publicationRecord(id) }

    private suspend fun <T> guard(request: suspend () -> T): T = try {
        request()
    } catch (error: CancellationException) {
        throw error
    } catch (_: HttpRequestTimeoutException) {
        throw LoadException.Timeout()
    } catch (_: ConnectTimeoutException) {
        throw LoadException.Timeout()
    } catch (_: SocketTimeoutException) {
        throw LoadException.Timeout()
    } catch (error: ResponseException) {
        if (error.response.status.value == 404) throw LoadException.NotFound()
        if (error.response.status.value in 400..499 && error.response.status.value != 429) {
            throw LoadException.Request(error.response.status.value)
        }
        throw LoadException.Server(error.response.status.value)
    } catch (_: PubMedApiException) {
        throw LoadException.Server()
    } catch (_: ContentConvertException) {
        throw LoadException.BadResponse()
    } catch (_: NoTransformationFoundException) {
        throw LoadException.BadResponse()
    } catch (_: SerializationException) {
        throw LoadException.BadResponse()
    } catch (_: IllegalArgumentException) {
        throw LoadException.BadResponse()
    } catch (_: Throwable) {
        throw LoadException.NoConnection()
    }
}
