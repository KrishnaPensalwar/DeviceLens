package com.example.devicelens.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.devicelens.presentation.dashboard.DashboardScreen
import com.example.devicelens.presentation.device.DeviceInfoScreen
import com.example.devicelens.presentation.device.DeviceInfoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicelens.presentation.battery.BatteryScreen
import com.example.devicelens.presentation.network.NetworkScreen
import com.example.devicelens.presentation.storage.StorageScreen
import com.example.devicelens.presentation.health.HealthScreen

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
            DashboardScreen(
                onDeviceInfoClick = {
                    navController.navigate(Routes.Device.route)
                },
                onAppsClick = {
                    navController.navigate(Routes.Battery.route)
                },
                onNetworkClick = {
                    navController.navigate(Routes.Network.route)
                },
                onStorageClick = {
                    navController.navigate(Routes.Storage.route)
                },
                onHealthClick = {
                    navController.navigate(Routes.Health.route)
                }
            )
        }

        composable(
            route = Routes.Device.route
        ) {
            DeviceInfoScreen()
        }

        composable(
            route = Routes.Battery.route
        ) {
            BatteryScreen()
        }

        composable(
            route = Routes.Network.route
        ) {
            NetworkScreen()
        }

        composable(
            route = Routes.Storage.route
        ) {
            StorageScreen()
        }

        composable(
            route = Routes.Health.route
        ) {
            HealthScreen()
        }
    }
}