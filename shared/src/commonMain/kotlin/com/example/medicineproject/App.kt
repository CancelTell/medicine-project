package com.example.medicineproject

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.example.medicineproject.data.ArticleRepositoryImpl
import com.example.medicineproject.data.api.PubMedApi
import com.example.medicineproject.data.datasource.NetworkArticleDataSource
import com.example.medicineproject.data.network.pubMedHttpClient
import com.example.medicineproject.ui.detail.ArticleDetailViewModelFactory
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.ui.list.ArticleListViewModelFactory
import com.example.medicineproject.ui.components.AppScaffold
import com.example.medicineproject.navigation.AppNavDisplay
import com.example.medicineproject.navigation.Navigator
import com.example.medicineproject.ui.theme.MedicineAppTheme

@Composable
fun App() {
    MedicineAppTheme {
        val client = remember { pubMedHttpClient(BuildKonfig.PUBMED_BASE_URL) }
        DisposableEffect(client) {
            onDispose { client.close() }
        }
        val repository: ArticleRepository = remember(client) {
            ArticleRepositoryImpl(NetworkArticleDataSource(PubMedApi(client)))
        }
        val navigator = remember { Navigator() }
        val listViewModelFactory = remember { ArticleListViewModelFactory(navigator, repository) }
        val detailViewModelFactory = remember { ArticleDetailViewModelFactory(navigator, repository) }

        AppScaffold { innerPadding ->
            AppNavDisplay(
                modifier = Modifier.padding(innerPadding),
                navigator = navigator,
                listViewModelFactory = listViewModelFactory,
                detailViewModelFactory = detailViewModelFactory,
            )
        }
    }
}
