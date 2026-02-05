package com.example.smartthings.domain.port.out

import com.example.smartthings.domain.model.Device

interface LoadDevicesPort {
    suspend fun loadDevices(): List<Device>
}
