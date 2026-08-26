package com.example.devicelens.data.repository

import com.example.devicelens.data.system.UsageStatsProvider
import com.example.devicelens.domain.model.AppUsageDetail
import com.example.devicelens.domain.model.UsageOverview
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.repository.UsageRepository
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val usageStatsProvider: UsageStatsProvider
) : UsageRepository {

    override fun hasUsageAccess(): Boolean = usageStatsProvider.hasUsageAccess()

    override suspend fun getUsageOverview(period: UsagePeriod): UsageOverview {
        return usageStatsProvider.getUsageOverview(period)
    }

    override suspend fun getAppUsageDetail(
        packageName: String,
        period: UsagePeriod
    ): AppUsageDetail {
        return usageStatsProvider.getAppUsageDetail(packageName, period)
    }
}
