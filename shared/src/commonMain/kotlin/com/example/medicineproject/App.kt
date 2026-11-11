package com.example.medicineproject

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.medicineproject.data.ArticleRepositoryImpl
import com.example.medicineproject.detail.ArticleDetailViewModelFactory
import com.example.medicineproject.domain.ArticleRepository
import com.example.medicineproject.list.ArticleListViewModelFactory
import com.example.medicineproject.ui.components.AppScaffold
import com.example.medicineproject.ui.navigation.AppNavDisplay
import com.example.medicineproject.ui.navigation.Navigator
import com.example.medicineproject.ui.theme.MedicineAppTheme

@Composable
fun App() {
    MedicineAppTheme {
        val repository: ArticleRepository = remember { ArticleRepositoryImpl() }
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