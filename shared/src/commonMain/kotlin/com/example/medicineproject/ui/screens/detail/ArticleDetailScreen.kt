package com.example.medicineproject.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medicineproject.resources.Res
import com.example.medicineproject.resources.article_author
import com.example.medicineproject.resources.article_not_found
import com.example.medicineproject.resources.back
import com.example.medicineproject.resources.no_related_articles
import com.example.medicineproject.resources.publication_record
import com.example.medicineproject.resources.related_articles
import com.example.medicineproject.ui.components.ArticleCard
import com.example.medicineproject.ui.components.CenteredContent
import com.example.medicineproject.ui.components.LoadErrorView
import com.example.medicineproject.ui.detail.ArticleDetailIntent
import com.example.medicineproject.ui.detail.ArticleDetailState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArticleDetailScreen(
    state: ArticleDetailState,
    onIntent: (ArticleDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { onIntent(ArticleDetailIntent.BackClicked) }) {
            Text(stringResource(Res.string.back))
        }
        Spacer(Modifier.height(12.dp))
        when (state) {
            ArticleDetailState.Loading -> CenteredContent { CircularProgressIndicator() }
            ArticleDetailState.NotFound -> CenteredContent { Text(stringResource(Res.string.article_not_found)) }
            is ArticleDetailState.Error -> CenteredContent {
                LoadErrorView(state.error, onRetry = { onIntent(ArticleDetailIntent.Retry) })
            }
            is ArticleDetailState.Success -> ArticleDetailContent(state, onIntent)
        }
    }
}

@Composable
private fun ArticleDetailContent(state: ArticleDetailState.Success, onIntent: (ArticleDetailIntent) -> Unit) {
    val article = state.article
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(article.journal, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(article.title, style = MaterialTheme.typography.headlineSmall)
        if (article.author.isNotBlank()) Text(stringResource(Res.string.article_author, article.author))
        if (article.publicationDate.isNotBlank()) Text(article.publicationDate)
        if (article.description.isNotBlank()) {
            Text(stringResource(Res.string.publication_record), style = MaterialTheme.typography.titleMedium)
            SelectionContainer { Text(article.description, style = MaterialTheme.typography.bodyMedium) }
        }
        Text(stringResource(Res.string.related_articles), style = MaterialTheme.typography.titleLarge)
        when {
            state.isLoadingRelated -> CircularProgressIndicator()
            state.relatedError != null -> LoadErrorView(
                state.relatedError,
                onRetry = { onIntent(ArticleDetailIntent.RetryRelated) },
            )
            article.related.isEmpty() -> Text(stringResource(Res.string.no_related_articles))
            else -> article.related.forEach { related ->
                ArticleCard(
                    article = related,
                    onClick = { onIntent(ArticleDetailIntent.RelatedClicked(related.id)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
