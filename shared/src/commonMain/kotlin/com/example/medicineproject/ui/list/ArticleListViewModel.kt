package com.example.medicineproject.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.domain.LoadArticlesUseCase
import com.example.medicineproject.domain.errors.LoadException
import com.example.medicineproject.navigation.Navigator
import com.example.medicineproject.navigation.Screen
import com.example.medicineproject.ui.model.toCardUi
import com.example.medicineproject.ui.model.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleListViewModel(
    private val navigator: Navigator,
    private val repository: ArticleRepository,
    private val mappingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _state = MutableStateFlow(ArticleListState())
    val state: StateFlow<ArticleListState> = _state.asStateFlow()
    private var loadJob: Job? = null
    private var nextPageJob: Job? = null
    private var pager = LoadArticlesUseCase(repository, "")

    init {
        load()
    }

    fun onIntent(intent: ArticleListIntent) {
        when (intent) {
            is ArticleListIntent.ArticleClicked -> navigator.open(Screen.ArticleDetail(intent.id))
            is ArticleListIntent.QueryChanged -> {
                if (intent.value == _state.value.query) return
                _state.update { it.copy(query = intent.value) }
                load(debounce = true)
            }
            ArticleListIntent.Retry -> load()
            ArticleListIntent.LoadMore -> loadMore()
        }
    }

    private fun load(debounce: Boolean = false) {
        loadJob?.cancel()
        nextPageJob?.cancel()
        val currentPager = LoadArticlesUseCase(repository, _state.value.query)
        pager = currentPager
        _state.update { it.copy(content = ArticleListContent.Loading) }
        loadJob = viewModelScope.launch {
            if (debounce) delay(400)
            val content = try {
                val page = currentPager.loadNext() ?: return@launch
                val cards = withContext(mappingDispatcher) { page.items.map { it.toCardUi() }.distinctBy { it.id } }
                ArticleListContent.Success(cards, canLoadMore = page.nextOffset != null)
            } catch (error: LoadException) {
                ArticleListContent.Error(error.toUi())
            }
            _state.update { it.copy(content = content) }
        }
    }

    private fun loadMore() {
        if (loadJob?.isActive == true) return
        val current = _state.value.content as? ArticleListContent.Success ?: return
        if (!current.canLoadMore || current.isLoadingMore) return
        val currentPager = pager
        _state.update { it.copy(content = current.copy(isLoadingMore = true, loadMoreError = null)) }
        nextPageJob = viewModelScope.launch {
            val content = try {
                val page = currentPager.loadNext() ?: return@launch
                val cards = withContext(mappingDispatcher) {
                    (current.articles + page.items.map { it.toCardUi() }).distinctBy { it.id }
                }
                ArticleListContent.Success(cards, canLoadMore = page.nextOffset != null)
            } catch (error: LoadException) {
                current.copy(isLoadingMore = false, loadMoreError = error.toUi())
            }
            _state.update { it.copy(content = content) }
        }
    }
}
