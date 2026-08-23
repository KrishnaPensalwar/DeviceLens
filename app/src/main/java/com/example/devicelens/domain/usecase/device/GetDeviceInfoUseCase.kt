package com.example.devicelens.domain.usecase.device

import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.repository.DeviceRepository

class GetDeviceInfoUseCase (
    private val repository: DeviceRepository
){
    fun invoke() : DeviceInfo{
        return repository.getDeviceInfo()
    }
}