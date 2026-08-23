package com.example.devicelens.data.system

import android.os.Build
import com.example.devicelens.domain.model.DeviceInfo

class DeviceInfoProvider{

    fun getDeviceInfo() : DeviceInfo{
        return DeviceInfo(
            Build.MODEL,
            Build.MANUFACTURER,
            Build.VERSION.RELEASE,
            Build.VERSION.SECURITY_PATCH
        )
    }
}