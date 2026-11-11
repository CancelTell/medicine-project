package com.example.medicineproject.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.ui.navigation.Navigator
import kotlin.reflect.KClass

class ArticleListViewModelFactory(
    private val navigator: Navigator,
    private val repository: ArticleRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return ArticleListViewModel(navigator = navigator, repository = repository) as T
    }
}