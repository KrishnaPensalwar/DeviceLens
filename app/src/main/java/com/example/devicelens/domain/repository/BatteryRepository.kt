package com.example.devicelens.domain.repository

import com.example.devicelens.domain.model.BatteryInfo

interface BatteryRepository{
    fun getBatteryInfo() : BatteryInfo
}