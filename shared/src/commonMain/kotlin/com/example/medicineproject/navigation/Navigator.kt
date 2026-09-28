package com.example.medicineproject.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator {
    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.ArticleList))
    val backStack = _backStack.asStateFlow()


    fun open(screen : Screen){
        _backStack.update { it + screen}
    }

    fun back(){
        _backStack.update { currentStack ->
            if (currentStack.size > 1) {
                currentStack.dropLast(1)
            } else {
                currentStack
            }
        }
    }
}