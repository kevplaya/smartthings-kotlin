package com.example.smartthings.service

import com.example.smartthings.client.SmartThingsClient
import com.example.smartthings.web.dto.DeviceDto
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val smartThingsClient: SmartThingsClient
) {
    fun getDevices(): List<DeviceDto> {
        val response = smartThingsClient.getDevices()
        return response.items.map { DeviceDto.from(it) }
    }
}
