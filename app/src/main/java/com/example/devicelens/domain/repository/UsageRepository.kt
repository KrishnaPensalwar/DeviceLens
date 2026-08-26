package com.example.devicelens.domain.repository

import com.example.devicelens.domain.model.AppUsageDetail
import com.example.devicelens.domain.model.UsageOverview
import com.example.devicelens.domain.model.UsagePeriod

interface UsageRepository {
    fun hasUsageAccess(): Boolean
    suspend fun getUsageOverview(period: UsagePeriod): UsageOverview
    suspend fun getAppUsageDetail(packageName: String, period: UsagePeriod): AppUsageDetail
}
