package com.example.devicelens.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.devicelens.presentation.battery.BatteryScreen
import com.example.devicelens.presentation.components.LensTab
import com.example.devicelens.presentation.dashboard.DashboardScreen
import com.example.devicelens.presentation.device.DeviceInfoScreen
import com.example.devicelens.presentation.health.HealthScreen
import com.example.devicelens.presentation.network.NetworkScreen
import com.example.devicelens.presentation.storage.StorageScreen
import com.example.devicelens.presentation.usage.UsageDetailScreen
import com.example.devicelens.presentation.usage.UsageScreen

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
                },
                onUsageClick = {
                    navController.navigate(Routes.Usage.route)
                },
                onTabSelected = { tab -> navController.navigateTab(tab) }
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
            NetworkScreen(
                onBack = { navController.popBackStack() },
                onTabSelected = { tab -> navController.navigateTab(tab) }
            )
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

        composable(
            route = Routes.Usage.route
        ) {
            UsageScreen(
                onAppClick = { packageName ->
                    navController.navigate(usageDetailRoute(packageName))
                }
            )
        }

        composable(
            route = "usage/{packageName}",
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            UsageDetailScreen()
        }
    }
}

private fun NavHostController.navigateTab(tab: LensTab) {
    val route = when (tab) {
        LensTab.Diagnostics -> Routes.Dashboard.route
        LensTab.Sensors -> Routes.Device.route
        LensTab.Storage -> Routes.Storage.route
        LensTab.Network -> Routes.Network.route
    }
    navigate(route) {
        popUpTo(Routes.Dashboard.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}