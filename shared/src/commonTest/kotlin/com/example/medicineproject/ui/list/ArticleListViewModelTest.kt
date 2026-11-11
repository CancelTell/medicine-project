package com.example.medicineproject.ui.list

import androidx.lifecycle.ViewModelStore
import com.example.medicineproject.TestArticleRepository
import com.example.medicineproject.article
import com.example.medicineproject.domain.ArticlePage
import com.example.medicineproject.domain.errors.LoadException
import com.example.medicineproject.navigation.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ArticleListViewModelTest {
    @Test
    fun typingCancelsPreviousRequestAndWaitsForLastInput() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = ViewModelStore()
        try {
            var initialCancelled = false
            val queries = mutableListOf<String>()
            val repository = TestArticleRepository().apply {
                pages = { query, _, _ ->
                    queries += query
                    if (query.isEmpty()) {
                        try { awaitCancellation() } finally { initialCancelled = true }
                    }
                    ArticlePage(listOf(article(query)), null)
                }
            }
            val vm = ArticleListViewModel(Navigator(), repository, dispatcher)
            store.put("list", vm)
            runCurrent()
            vm.onIntent(ArticleListIntent.QueryChanged("cancer"))
            runCurrent()
            assertTrue(initialCancelled)
            advanceTimeBy(399)
            vm.onIntent(ArticleListIntent.QueryChanged("asthma"))
            runCurrent()
            advanceTimeBy(399)
            runCurrent()
            assertEquals(listOf(""), queries)
            advanceUntilIdle()
            assertEquals(listOf("", "asthma"), queries)
            assertEquals("asthma", vm.state.value.query)
            assertEquals("asthma", assertIs<ArticleListContent.Success>(vm.state.value.content).articles.single().id)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun paginationFailureKeepsCardsAndRetryDoesNotDuplicateThem() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = ViewModelStore()
        try {
            var fail = true
            val offsets = mutableListOf<Int>()
            val repository = TestArticleRepository().apply {
                pages = { _, offset, _ ->
                    offsets += offset
                    if (offset == 20 && fail) {
                        fail = false
                        throw LoadException.NoConnection()
                    }
                    if (offset == 0) ArticlePage(listOf(article("1")), 20)
                    else ArticlePage(listOf(article("1"), article("2")), null)
                }
            }
            val vm = ArticleListViewModel(Navigator(), repository, dispatcher)
            store.put("list", vm)
            advanceUntilIdle()
            vm.onIntent(ArticleListIntent.LoadMore)
            advanceUntilIdle()
            val failed = assertIs<ArticleListContent.Success>(vm.state.value.content)
            assertEquals(listOf("1"), failed.articles.map { it.id })
            assertNotNull(failed.loadMoreError)
            vm.onIntent(ArticleListIntent.LoadMore)
            vm.onIntent(ArticleListIntent.LoadMore)
            advanceUntilIdle()
            val success = assertIs<ArticleListContent.Success>(vm.state.value.content)
            assertEquals(listOf("1", "2"), success.articles.map { it.id })
            assertEquals(listOf(0, 20, 20), offsets)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun newQueryCancelsAnActiveNextPage() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val store = ViewModelStore()
        try {
            var cancelled = false
            val repository = TestArticleRepository().apply {
                pages = { query, offset, _ ->
                    if (offset > 0) {
                        try { awaitCancellation() } finally { cancelled = true }
                    }
                    ArticlePage(listOf(article(query.ifEmpty { "initial" })), if (query.isEmpty()) 20 else null)
                }
            }
            val vm = ArticleListViewModel(Navigator(), repository, dispatcher)
            store.put("list", vm)
            advanceUntilIdle()
            vm.onIntent(ArticleListIntent.LoadMore)
            runCurrent()
            vm.onIntent(ArticleListIntent.QueryChanged("new"))
            advanceUntilIdle()
            assertTrue(cancelled)
            assertEquals("new", assertIs<ArticleListContent.Success>(vm.state.value.content).articles.single().id)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
