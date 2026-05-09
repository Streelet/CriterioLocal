package com.example.criteriolocal.ui.app

import androidx.compose.runtime.Composable
import com.example.criteriolocal.core.di.AppContainer
import com.example.criteriolocal.ui.navigation.CriterioLocalNavGraph

@Composable
fun CriterioLocalApp(
    appContainer: AppContainer,
) {
    CriterioLocalNavGraph(appContainer = appContainer)
}
