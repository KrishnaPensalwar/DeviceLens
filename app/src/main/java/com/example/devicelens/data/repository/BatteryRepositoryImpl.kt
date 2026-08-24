package com.example.devicelens.data.repository

import com.example.devicelens.data.system.BatteryManagerProvider
import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.domain.repository.BatteryRepository
import javax.inject.Inject

class BatteryRepositoryImpl @Inject constructor(
    private val batteryManagerProvider: BatteryManagerProvider
) : BatteryRepository{
    override fun getBatteryInfo(): BatteryInfo {
        return batteryManagerProvider.getBatteryInfo()
    }

}