package com.example.smartthings.port

import com.example.smartthings.web.dto.DevicesResponse

/**
 * Port for fetching device list from an external source (e.g. SmartThings API).
 * Implementations are adapters (e.g. SmartThingsClient).
 */
interface DeviceSource {
    suspend fun getDevices(accessToken: String): DevicesResponse
}
