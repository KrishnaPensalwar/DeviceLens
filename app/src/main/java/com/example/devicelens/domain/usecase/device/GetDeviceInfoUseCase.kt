package com.example.devicelens.domain.usecase.device

import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.repository.DeviceRepository
import javax.inject.Inject

class GetDeviceInfoUseCase @Inject constructor(
    private val repository: DeviceRepository
) {

    operator fun invoke(): DeviceInfo {
        return repository.getDeviceInfo()
    }
}