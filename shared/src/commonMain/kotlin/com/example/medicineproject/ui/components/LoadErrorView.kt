package com.example.medicineproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medicineproject.resources.Res
import com.example.medicineproject.resources.retry
import com.example.medicineproject.ui.model.LoadErrorUi
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadErrorView(error: LoadErrorUi, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(error.message))
        Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
    }
}
