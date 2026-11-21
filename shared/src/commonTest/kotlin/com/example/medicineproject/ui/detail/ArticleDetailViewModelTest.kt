package com.example.medicineproject.ui.detail

import androidx.lifecycle.ViewModelStore
import com.example.medicineproject.TestArticleRepository
import com.example.medicineproject.article
import com.example.medicineproject.domain.errors.LoadException
import com.example.medicineproject.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {
    @Test
    fun removingViewModelCancelsItsRequestWithoutShowingAnError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = ViewModelStore()
        try {
            var cancelled = false
            val repository = TestArticleRepository().apply {
                detail = { try { awaitCancellation() } finally { cancelled = true } }
            }
            val vm = ArticleDetailViewModel("1", Navigator(), repository, dispatcher)
            store.put("detail", vm)
            runCurrent()
            store.clear()
            runCurrent()
            assertTrue(cancelled)
            assertIs<ArticleDetailState.Loading>(vm.state.value)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun relatedFailureKeepsArticleAndCanBeRetriedSeparately() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = ViewModelStore()
        try {
            var detailRequests = 0
            var fail = true
            val repository = TestArticleRepository().apply {
                detail = { detailRequests++; article(it) }
                related = {
                    if (fail) { fail = false; throw LoadException.NoConnection() }
                    listOf(article("2"))
                }
            }
            val vm = ArticleDetailViewModel("1", Navigator(), repository, dispatcher)
            store.put("detail", vm)
            advanceUntilIdle()
            val failed = assertIs<ArticleDetailState.Success>(vm.state.value)
            assertEquals("Article 1", failed.article.title)
            assertNotNull(failed.relatedError)
            vm.onIntent(ArticleDetailIntent.RetryRelated)
            advanceUntilIdle()
            val success = assertIs<ArticleDetailState.Success>(vm.state.value)
            assertNull(success.relatedError)
            assertFalse(success.isLoadingRelated)
            assertEquals("2", success.article.related.single().id)
            assertEquals(1, detailRequests)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
