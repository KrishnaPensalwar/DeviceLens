package com.example.devicelens.domain.usecase.usage

import com.example.devicelens.domain.model.AppUsageDetail
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.repository.UsageRepository
import javax.inject.Inject

class GetUsageHistoryUseCase @Inject constructor(
    private val usageRepository: UsageRepository
) {
    suspend fun invoke(packageName: String, period: UsagePeriod): AppUsageDetail {
        return usageRepository.getAppUsageDetail(packageName, period)
    }
}
