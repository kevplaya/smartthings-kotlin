package com.example.smartthings.adapter.`in`.web.controller

import com.example.smartthings.adapter.`in`.web.dto.DeviceDto
import com.example.smartthings.domain.port.`in`.GetDevicesUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DeviceController(
    private val getDevicesUseCase: GetDevicesUseCase
) {
    @GetMapping("/devices")
    suspend fun getDevices(): List<DeviceDto> =
        getDevicesUseCase.getDevices().map { DeviceDto.from(it) }
}
