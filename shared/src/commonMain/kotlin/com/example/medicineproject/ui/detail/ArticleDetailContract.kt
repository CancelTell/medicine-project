package com.example.medicineproject.ui.detail

import androidx.compose.runtime.Immutable
import com.example.medicineproject.ui.model.ArticleDetailUi
import com.example.medicineproject.ui.model.LoadErrorUi

@Immutable
sealed interface ArticleDetailState {
    data object Loading : ArticleDetailState
    data object NotFound : ArticleDetailState
    data class Error(val error: LoadErrorUi) : ArticleDetailState
    data class Success(
        val article: ArticleDetailUi,
        val isLoadingRelated: Boolean = true,
        val relatedError: LoadErrorUi? = null,
    ) : ArticleDetailState
}

sealed interface ArticleDetailIntent {
    data object BackClicked : ArticleDetailIntent
    data class RelatedClicked(val id: String) : ArticleDetailIntent
    data object Retry : ArticleDetailIntent
    data object RetryRelated : ArticleDetailIntent
}
