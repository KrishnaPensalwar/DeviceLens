package com.example.devicelens.data.system

import android.os.Build
import com.example.devicelens.domain.model.DeviceInfo
import javax.inject.Inject

class DeviceInfoProvider @Inject constructor() {

    fun getDeviceInfo() : DeviceInfo{
        return DeviceInfo(
            Build.MODEL,
            Build.MANUFACTURER,
            Build.VERSION.RELEASE,
            Build.VERSION.SECURITY_PATCH
        )
    }
}