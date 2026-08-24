package com.example.devicelens.domain.usecase.battery

import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.domain.repository.BatteryRepository
import javax.inject.Inject

class GetBatteryInfoUseCase @Inject constructor(
    private val batteryRepository: BatteryRepository
) {
    fun invoke() : BatteryInfo {
        return batteryRepository.getBatteryInfo()
    }
}