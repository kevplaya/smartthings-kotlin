package com.example.smartthings.web

import com.example.smartthings.service.DeviceService
import com.example.smartthings.web.dto.DeviceDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DeviceController(
    private val deviceService: DeviceService
) {
    @GetMapping("/devices")
    suspend fun getDevices(): List<DeviceDto> = deviceService.getDevices()
}
