package com.example.medicineproject.navigation

import com.example.medicineproject.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator {
    private var nextEntryId = 0L
    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.ArticleList))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    fun open(screen: Screen) {
        val entry = when (screen) {
            is Screen.ArticleDetail -> screen.copy(entryId = ++nextEntryId)
            Screen.ArticleList -> screen
        }
        _backStack.update { it + entry }
    }

    fun back() {
        _backStack.update { stack -> if (stack.size > 1) stack.dropLast(1) else stack }
    }
}
