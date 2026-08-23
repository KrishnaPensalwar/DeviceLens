package com.example.devicelens.domain.repository

import com.example.devicelens.domain.model.DeviceInfo

interface DeviceRepository {
    fun getDeviceInfo() : DeviceInfo
}