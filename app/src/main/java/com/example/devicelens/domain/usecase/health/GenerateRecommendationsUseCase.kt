package com.example.devicelens.domain.usecase.health

import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.HealthReport
import javax.inject.Inject

class GenerateRecommendationsUseCase @Inject constructor() {

    fun invoke(report: HealthReport): List<HealthRecommendation> {
        return report.issues
            .map { HealthRecommendation(title = it.detail, priority = it.priority) }
            .distinctBy { it.title }
            .sortedBy { it.priority }
    }
}
