package com.example.devicelens.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.devicelens.presentation.dashboard.DashboardScreen
import com.example.devicelens.presentation.device.DeviceInfoScreen
import com.example.devicelens.presentation.device.DeviceInfoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DeviceLensNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route
    ) {

        composable(
            route = Routes.Dashboard.route
        ) {
            DashboardScreen(){
                navController.navigate( Routes.Device.route)
            }
        }

        composable(
            route = Routes.Device.route
        ) {
            DeviceInfoScreen(
                viewModel = viewModel<DeviceInfoViewModel>()
            )
        }
    }
}