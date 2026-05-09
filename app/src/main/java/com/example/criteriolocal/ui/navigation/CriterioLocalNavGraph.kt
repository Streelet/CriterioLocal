package com.example.criteriolocal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.criteriolocal.core.di.AppContainer
import com.example.criteriolocal.ui.auth.LoginScreen
import com.example.criteriolocal.ui.auth.LoginViewModel
import com.example.criteriolocal.ui.auth.RegisterScreen
import com.example.criteriolocal.ui.auth.RegisterViewModel
import com.example.criteriolocal.ui.home.HomeScreen
import com.example.criteriolocal.ui.home.HomeViewModel
import com.example.criteriolocal.ui.profile.ProfileScreen
import com.example.criteriolocal.ui.profile.ProfileViewModel

@Composable
fun CriterioLocalNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Login,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Routes.Login) {
            val viewModel: LoginViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            LoginScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSubmit = {
                    viewModel.onSubmit()
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.Register) },
            )
        }

        composable(Routes.Register) {
            val viewModel: RegisterViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            RegisterScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSubmit = {
                    viewModel.onSubmit()
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.Home) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(appContainer),
            )
            val uiState by homeViewModel.uiState.collectAsState()
            HomeScreen(
                uiState = uiState,
                onOpenProfile = { navController.navigate(Routes.Profile) },
            )
        }

        composable(Routes.Profile) {
            val viewModel: ProfileViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            ProfileScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Routes.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
