package com.example.devicelens.domain.model


data class NetworkHealth(
    val score: Int,
    val title: String,
    val description: String
)

fun calculateNetworkHealth(
    internetAvailable: Boolean,
    signalQuality: SignalQuality,
    latencyMs: Long?,
    downloadMbps: Double?
): NetworkHealth {

    if (!internetAvailable) {
        return NetworkHealth(
            score = 0,
            title = "No Internet",
            description = "Your device is connected to a network, but internet access is unavailable."
        )
    }

    var score = 50

    // Signal
    score += when (signalQuality) {
        SignalQuality.EXCELLENT -> 20
        SignalQuality.GOOD -> 15
        SignalQuality.FAIR -> 8
        SignalQuality.POOR -> 0
        SignalQuality.UNKNOWN -> 5
    }

    // Latency
    score += when {
        latencyMs == null -> 0
        latencyMs < 50 -> 15
        latencyMs < 100 -> 10
        latencyMs < 200 -> 5
        else -> 0
    }

    // Speed
    score += when {
        downloadMbps == null -> 0
        downloadMbps >= 50 -> 15
        downloadMbps >= 20 -> 10
        downloadMbps >= 5 -> 5
        else -> 0
    }

    score = score.coerceIn(0, 100)

    return when {
        score >= 85 -> NetworkHealth(
            score = score,
            title = "Excellent",
            description = "Your network is working very well."
        )

        score >= 70 -> NetworkHealth(
            score = score,
            title = "Good",
            description = "Your network connection looks healthy."
        )

        score >= 50 -> NetworkHealth(
            score = score,
            title = "Fair",
            description = "Your network is usable but could be better."
        )

        else -> NetworkHealth(
            score = score,
            title = "Poor",
            description = "Your network connection may be causing problems."
        )
    }
}