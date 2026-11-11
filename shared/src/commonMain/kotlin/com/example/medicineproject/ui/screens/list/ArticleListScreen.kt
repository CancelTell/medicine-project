package com.example.medicineproject.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medicineproject.resources.Res
import com.example.medicineproject.resources.nothing_found
import com.example.medicineproject.ui.components.ArticleCard
import com.example.medicineproject.ui.components.ArticleSearchBar
import com.example.medicineproject.ui.components.CenteredContent
import com.example.medicineproject.ui.components.LoadErrorView
import com.example.medicineproject.ui.list.ArticleListContent
import com.example.medicineproject.ui.list.ArticleListIntent
import com.example.medicineproject.ui.list.ArticleListState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArticleListScreen(
    state: ArticleListState,
    onIntent: (ArticleListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        ArticleSearchBar(
            query = state.query,
            onQueryChange = { onIntent(ArticleListIntent.QueryChanged(it)) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        val contentModifier = Modifier.fillMaxWidth().weight(1f)
        when (val content = state.content) {
            ArticleListContent.Loading -> CenteredContent(contentModifier) { CircularProgressIndicator() }
            is ArticleListContent.Error -> CenteredContent(contentModifier) {
                LoadErrorView(content.error, onRetry = { onIntent(ArticleListIntent.Retry) })
            }
            is ArticleListContent.Success -> {
                if (content.articles.isEmpty()) {
                    CenteredContent(contentModifier) { Text(stringResource(Res.string.nothing_found)) }
                } else {
                    ArticleListContent(content, onIntent, contentModifier)
                }
            }
        }
    }
}

@Composable
private fun ArticleListContent(
    content: ArticleListContent.Success,
    onIntent: (ArticleListIntent) -> Unit,
    modifier: Modifier,
) {
    val listState = rememberLazyListState()
    val lastVisibleIndex by remember(listState) {
        derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
    }
    LaunchedEffect(lastVisibleIndex, content.articles.size, content.canLoadMore, content.isLoadingMore, content.loadMoreError) {
        if (lastVisibleIndex >= content.articles.size - 3 &&
            content.canLoadMore && !content.isLoadingMore && content.loadMoreError == null
        ) {
            onIntent(ArticleListIntent.LoadMore)
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(content.articles, key = { it.id }) { article ->
            ArticleCard(article, onClick = { onIntent(ArticleListIntent.ArticleClicked(article.id)) })
        }
        if (content.isLoadingMore) {
            item(key = "loading") {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        content.loadMoreError?.let { error ->
            item(key = "error") {
                LoadErrorView(error, onRetry = { onIntent(ArticleListIntent.LoadMore) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
