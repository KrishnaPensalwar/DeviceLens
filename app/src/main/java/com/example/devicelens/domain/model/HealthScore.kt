package com.example.devicelens.domain.model

data class HealthReport(
    val score: Int,
    val status: HealthStatus,
    val summary: String,
    val attentionCount: Int,
    val areas: List<HealthArea>,
    val issues: List<HealthIssue>,
    val checkedAtMillis: Long
)

data class HealthArea(
    val title: String,
    val status: HealthStatus,
    val description: String,
    val score: Int
)

data class HealthIssue(
    val title: String,
    val detail: String,
    val priority: RecommendationPriority
)

data class HealthRecommendation(
    val title: String,
    val priority: RecommendationPriority
)

enum class HealthStatus {
    EXCELLENT,
    GOOD,
    NEEDS_ATTENTION,
    POOR
}

enum class RecommendationPriority {
    HIGH,
    MEDIUM,
    LOW
}
