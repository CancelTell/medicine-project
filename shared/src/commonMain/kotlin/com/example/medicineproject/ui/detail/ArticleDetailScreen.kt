package com.example.medicineproject.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.medicineproject.detail.ArticleDetailIntent
import com.example.medicineproject.detail.ArticleDetailState
import com.example.medicineproject.resources.Res
import com.example.medicineproject.resources.article_author
import com.example.medicineproject.resources.article_not_found
import com.example.medicineproject.resources.back
import com.example.medicineproject.ui.components.CenteredContent
import com.example.medicineproject.ui.model.ArticleDetailUi
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArticleDetailScreen(
    state: ArticleDetailState,
    onIntent: (ArticleDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Button(
            onClick = { onIntent(ArticleDetailIntent.BackClicked) },
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(stringResource(Res.string.back))
        }

        Spacer(modifier = Modifier.height(12.dp))

        val article = state.article
        when {
            state.isLoading -> CenteredContent { CircularProgressIndicator() }
            article == null -> CenteredContent { Text(stringResource(Res.string.article_not_found)) }
            else -> ArticleDetailContent(article = article)
        }
    }
}

@Composable
private fun ArticleDetailContent(
    article: ArticleDetailUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary,
        ) {
            Text(
                text = article.category,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.article_author, article.author),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondary,
        ) {
            Text(
                text = article.description,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}