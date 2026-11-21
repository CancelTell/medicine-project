package com.example.medicineproject.domain

import com.example.medicineproject.TestArticleRepository
import com.example.medicineproject.article
import com.example.medicineproject.domain.errors.LoadException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LoadArticlesUseCaseTest {
    @Test
    fun failedPageIsRetriedAtTheSameOffsetAndEndStopsRequests() = runTest {
        val offsets = mutableListOf<Int>()
        var fail = true
        val repository = TestArticleRepository().apply {
            pages = { _, offset, _ ->
                offsets += offset
                if (offset == 20 && fail) {
                    fail = false
                    throw LoadException.NoConnection()
                }
                ArticlePage(listOf(article("$offset")), if (offset == 0) 20 else null)
            }
        }
        val pager = LoadArticlesUseCase(repository, "medicine")
        pager.loadNext()
        assertFailsWith<LoadException.NoConnection> { pager.loadNext() }
        pager.loadNext()
        assertNull(pager.loadNext())
        assertEquals(listOf(0, 20, 20), offsets)
    }

    @Test
    fun repeatedScrollRequestsDoNotStartTheSamePageTwice() = runTest {
        val finish = CompletableDeferred<Unit>()
        var requests = 0
        val repository = TestArticleRepository().apply {
            pages = { _, _, _ ->
                requests++
                finish.await()
                ArticlePage(emptyList(), null)
            }
        }
        val pager = LoadArticlesUseCase(repository, "medicine")
        val first = async { pager.loadNext() }
        runCurrent()
        assertNull(pager.loadNext())
        assertEquals(1, requests)
        finish.complete(Unit)
        first.await()
    }
}
