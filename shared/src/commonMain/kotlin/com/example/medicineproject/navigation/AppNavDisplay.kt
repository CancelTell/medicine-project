package com.example.medicineproject.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicineproject.detail.ArticleDetailViewModel
import com.example.medicineproject.detail.ArticleDetailViewModelFactory
import com.example.medicineproject.list.ArticleListViewModel
import com.example.medicineproject.list.ArticleListViewModelFactory
import com.example.medicineproject.ui.screens.detail.ArticleDetailScreen
import com.example.medicineproject.ui.screens.list.ArticleListScreen
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.medicineproject.Screen

private const val TRANSITION_MS = 300

@Composable
fun AppNavDisplay(
    modifier: Modifier,
    navigator: Navigator,
    listViewModelFactory: ArticleListViewModelFactory,
    detailViewModelFactory: ArticleDetailViewModelFactory,
) {
    val backStack by navigator.backStack.collectAsStateWithLifecycle()
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = navigator::back,
        transitionSpec = { slide(SlideDirection.Start) },
        popTransitionSpec = { slide(SlideDirection.End) },
        predictivePopTransitionSpec = { slide(SlideDirection.End) },
        entryProvider = entryProvider {
            entry<Screen.ArticleList> {
                val viewModel: ArticleListViewModel = viewModel(factory = listViewModelFactory)
                val state by viewModel.state.collectAsStateWithLifecycle()
                ArticleListScreen(state = state, onIntent = viewModel::onIntent)
            }
            entry<Screen.ArticleDetail> { key ->
                val viewModel: ArticleDetailViewModel = viewModel(
                    key = "detail-${key.id}",
                    factory = detailViewModelFactory,
                    extras = ArticleDetailViewModelFactory.extrasFor(key.id),
                )
                val state by viewModel.state.collectAsStateWithLifecycle()
                ArticleDetailScreen(state = state, onIntent = viewModel::onIntent)
            }
        },
    )
}

private fun AnimatedContentTransitionScope<*>.slide(direction: SlideDirection): ContentTransform =
    (slideIntoContainer(direction, tween(TRANSITION_MS)) + fadeIn(tween(TRANSITION_MS)))
        .togetherWith(
            slideOutOfContainer(direction, tween(TRANSITION_MS)) + fadeOut(tween(TRANSITION_MS)),
        )