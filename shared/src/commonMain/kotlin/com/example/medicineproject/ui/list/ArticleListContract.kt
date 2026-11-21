package com.example.medicineproject.ui.list

import androidx.compose.runtime.Immutable
import com.example.medicineproject.ui.model.ArticleCardUi
import com.example.medicineproject.ui.model.LoadErrorUi

@Immutable
data class ArticleListState(
    val query: String = "",
    val content: ArticleListContent = ArticleListContent.Loading,
)

@Immutable
sealed interface ArticleListContent {
    data object Loading : ArticleListContent
    data class Error(val error: LoadErrorUi) : ArticleListContent
    data class Success(
        val articles: List<ArticleCardUi>,
        val canLoadMore: Boolean,
        val isLoadingMore: Boolean = false,
        val loadMoreError: LoadErrorUi? = null,
    ) : ArticleListContent
}

sealed interface ArticleListIntent {
    data class QueryChanged(val value: String) : ArticleListIntent
    data class ArticleClicked(val id: String) : ArticleListIntent
    data object Retry : ArticleListIntent
    data object LoadMore : ArticleListIntent
}
