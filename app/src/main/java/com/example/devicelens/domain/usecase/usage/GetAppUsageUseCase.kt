package com.example.devicelens.domain.usecase.usage

import com.example.devicelens.domain.model.UsageOverview
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.repository.UsageRepository
import javax.inject.Inject

class GetAppUsageUseCase @Inject constructor(
    private val usageRepository: UsageRepository
) {
    fun hasAccess(): Boolean = usageRepository.hasUsageAccess()

    suspend fun invoke(period: UsagePeriod): UsageOverview {
        return usageRepository.getUsageOverview(period)
    }
}
