package com.example.medicineproject.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        Button(
            onClick = { onIntent(ArticleDetailIntent.BackClicked) },
            shape = RoundedCornerShape(12.dp),
        ) {
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
    ) {
        if (article.journal.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    text = article.journal,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (article.author.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.article_author, article.author),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        if (article.publicationDate.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = article.publicationDate,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        if (article.description.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            Text(stringResource(Res.string.publication_record), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondary,
            ) {
                SelectionContainer {
                    Text(
                        text = article.description,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.related_articles),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(12.dp))
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
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
