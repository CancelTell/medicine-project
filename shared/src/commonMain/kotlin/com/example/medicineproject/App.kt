package com.example.medicineproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicineproject.navigation.AppNavDisplay
import com.example.medicineproject.navigation.Navigator
import com.example.medicineproject.ui.theme.MedicineAppTheme

@Composable
fun App() {
    MedicineAppTheme {
        val navigator = remember { Navigator() }
        val backStack by navigator.backStack.collectAsStateWithLifecycle()

        Box(modifier = Modifier.fillMaxSize()) {
            AppNavDisplay(
                navigator = navigator,
                backStack = backStack
            )
        }
    }
}
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        if (selectedArticle.value == null) {
//            ArticleListScreen(
//                articles = mockArticles,
//                onArticleClick = { article ->
//                    selectedArticle.value = article }
//            )
//        } else {
//            ArticleDetailScreen(
//                article = selectedArticle.value!!,
//                onBackClick = {
//                    selectedArticle.value = null
//                }
//            )
//        }

//val currentScreen = remember { mutableStateOf<Screen>(Screen.ArticleList) }
//
//when (currentScreen.value) {
//    is Screen.ArticleList -> {
//        ArticleListScreen(
//            articles = mockArticles,
//        ) { article -> currentScreen.value = Screen.ArticleDetail(article) }
//    }
//
//    is Screen.ArticleDetail -> {
//        val article = (currentScreen.value as Screen.ArticleDetail).article
//        ArticleDetailScreen(
//            article = article,
//            onBackClick = { currentScreen.value = Screen.ArticleList }
//        )
//    }

