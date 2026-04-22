package com.example.criteriolocal.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.criteriolocal.core.di.AppContainer
import com.example.criteriolocal.ui.home.HomeScreen
import com.example.criteriolocal.ui.home.HomeViewModel

@Composable
fun CriterioLocalApp(
    appContainer: AppContainer,
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory(appContainer),
    )
    val uiState by homeViewModel.uiState.collectAsState()

    HomeScreen(uiState = uiState)
}
