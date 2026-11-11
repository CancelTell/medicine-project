package com.example.medicineproject.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medicineproject.list.ArticleListIntent
import com.example.medicineproject.list.ArticleListState
import com.example.medicineproject.resources.Res
import com.example.medicineproject.resources.nothing_found
import com.example.medicineproject.ui.components.ArticleCard
import com.example.medicineproject.ui.components.ArticleSearchBar
import com.example.medicineproject.ui.components.CenteredContent
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        when {
            state.isLoading -> CenteredContent { CircularProgressIndicator() }

            state.articles.isEmpty() -> CenteredContent { Text(stringResource(Res.string.nothing_found)) }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.articles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { onIntent(ArticleListIntent.ArticleClicked(article.id)) },
                    )
                }
            }
        }
    }
}