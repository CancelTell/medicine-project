package com.example.medicineproject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator {
    private val _navStack = MutableStateFlow<List<Screen>>(listOf(Screen.ArticleList))
    val navStack = _navStack.asStateFlow()

    fun addToBackStack(screen : Screen) {
        _navStack.update {it + screen}
    }

    fun back() {
        _navStack.update { currentStack ->
            if (currentStack.size > 1) {
                currentStack.dropLast(1)
            } else {
                currentStack
            }
        }
    }
}