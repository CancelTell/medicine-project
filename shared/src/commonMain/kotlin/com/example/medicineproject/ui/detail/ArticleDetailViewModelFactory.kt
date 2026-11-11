package com.example.medicineproject.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.ui.navigation.Navigator
import kotlin.reflect.KClass

class ArticleDetailViewModelFactory(
    private val navigator: Navigator,
    private val repository: ArticleRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val articleId = checkNotNull(extras[ArticleIdKey]) { "articleId is missing in CreationExtras" }
        @Suppress("UNCHECKED_CAST")
        return ArticleDetailViewModel(
            articleId = articleId,
            navigator = navigator,
            repository = repository,
        ) as T
    }

    companion object {
        val ArticleIdKey = CreationExtras.Key<String>()

        fun extrasFor(articleId: String): CreationExtras =
            MutableCreationExtras().apply { set(ArticleIdKey, articleId) }
    }
}