package com.example.devicelens.data.repository

import com.example.devicelens.data.system.DeviceInfoProvider
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceInfoProvider: DeviceInfoProvider
) : DeviceRepository {

    override fun getDeviceInfo(): DeviceInfo {
        return deviceInfoProvider.getDeviceInfo()
    }
}