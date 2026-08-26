package com.example.devicelens.presentation.navigation


sealed class Routes(
    val route: String
) {
    data object Dashboard : Routes("dashboard")
    data object Storage : Routes("storage")
    data object Battery : Routes("battery")
    data object Memory : Routes("memory")
    data object Usage : Routes("usage")
    data object Network : Routes("network")
    data object Device : Routes("device")
    data object Health : Routes("health")
}

fun usageDetailRoute(packageName: String): String {
    return "usage/${android.net.Uri.encode(packageName)}"
}