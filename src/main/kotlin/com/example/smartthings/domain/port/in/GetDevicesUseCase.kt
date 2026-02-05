package com.example.smartthings.domain.port.`in`

import com.example.smartthings.domain.model.Device

interface GetDevicesUseCase {
    suspend fun getDevices(): List<Device>
}
