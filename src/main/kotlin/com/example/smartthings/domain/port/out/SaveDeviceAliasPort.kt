package com.example.smartthings.domain.port.out

import com.example.smartthings.domain.model.DeviceAlias

interface SaveDeviceAliasPort {
    suspend fun save(deviceAlias: DeviceAlias): DeviceAlias
}
