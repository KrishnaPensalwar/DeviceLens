package com.example.devicelens.domain.model


enum class ConnectionType {
    WIFI,
    MOBILE,
    ETHERNET,
    VPN,
    NONE,
    UNKNOWN
}

enum class SignalQuality {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    UNKNOWN
}

data class NetworkInfo(
    val connectionType: ConnectionType = ConnectionType.NONE,
    val networkName: String? = null,
    val isInternetAvailable: Boolean = false,

    val signalDbm: Int? = null,
    val signalQuality: SignalQuality = SignalQuality.UNKNOWN,

    val ipAddress: String? = null,

    val downloadMbps: Double? = null,
    val uploadMbps: Double? = null,

    val latencyMs: Long? = null,

    val qualityScore: Int = 0
)