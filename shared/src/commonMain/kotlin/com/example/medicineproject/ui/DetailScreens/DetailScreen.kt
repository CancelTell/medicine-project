package com.example.medicineproject.ui.DetailScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.medicineproject.domain.Article

@Composable
fun ArticleDetailScreen(
    article: Article,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Back", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = article.category,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = article.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "By ${article.author}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondary
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = article.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    lineHeight = 24.sp
                )
            }
        }
    }
}