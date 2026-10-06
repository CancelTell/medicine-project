package com.example.medicineproject.ui.navigation

import com.example.medicineproject.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator {
    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.ArticleList))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    fun open(screen: Screen) {
        _backStack.update { it + screen }
    }

    fun back() {
        _backStack.update { stack -> if (stack.size > 1) stack.dropLast(1) else stack }
    }
}