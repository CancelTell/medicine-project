package com.example.medicineproject.navigation
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicineproject.ArticleDetailScreen
import com.example.medicineproject.ArticleDetailViewModel
import com.example.medicineproject.ArticleListViewModel
import com.example.medicineproject.Navigator
import com.example.medicineproject.Screen
import com.example.medicineproject.ArticleListScreen
@Composable
fun AppNavDisplay(
    modifier: Modifier = Modifier,
    navigator: Navigator,
    backStack: List<Screen>
) {
    AnimatedContent(
        targetState = backStack.lastOrNull(),
        transitionSpec = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(100)
            ) + fadeIn(tween(300)) togetherWith
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        tween(300)
                    ) + fadeOut(tween(0))
        }
    )
        { currentScreen ->
            when (currentScreen) {
                is Screen.ArticleList -> {
                    val viewModel: ArticleListViewModel = viewModel(
                        factory = androidx.lifecycle.viewmodel.viewModelFactory {
                            addInitializer(ArticleListViewModel::class) {
                                ArticleListViewModel(navigator)
                            }
                        }
                    )
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    ArticleListScreen(
                        articles = state.articles,
                        onArticleClick = viewModel::onArticleClick
                    )
                }

                is Screen.ArticleDetail -> {
                    val viewModel: ArticleDetailViewModel = viewModel(
                        key = "detail-${currentScreen.articleId}",
                        factory = androidx.lifecycle.viewmodel.viewModelFactory {
                            addInitializer(ArticleDetailViewModel::class) {
                                ArticleDetailViewModel(navigator, currentScreen.articleId)
                            }
                        }
                    )
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    state?.article?.let { article ->
                        ArticleDetailScreen(
                            article = article,
                            onBackClick = viewModel::onBack
                        )
                    }
                }

                null -> {

                }
            }
        }

}