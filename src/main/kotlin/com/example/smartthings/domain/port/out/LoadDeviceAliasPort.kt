package com.example.smartthings.domain.port.out

import com.example.smartthings.domain.model.DeviceAlias

interface LoadDeviceAliasPort {
    suspend fun loadByUserIdAndDeviceId(userId: String, deviceId: String): DeviceAlias?
}
