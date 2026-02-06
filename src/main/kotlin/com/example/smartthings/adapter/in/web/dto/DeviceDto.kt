package com.example.smartthings.adapter.`in`.web.dto

import com.example.smartthings.domain.model.Device

data class DeviceDto(
    val deviceId: String,
    val name: String,
    val label: String,
    val manufacturerCode: String?,
    val typeName: String?,
    val type: String?,
    val roomId: String?,
    val locationId: String?
) {
    companion object {
        fun from(device: Device): DeviceDto =
            DeviceDto(
                deviceId = device.deviceId,
                name = device.name,
                label = device.label,
                manufacturerCode = device.manufacturerCode,
                typeName = device.typeName,
                type = device.type,
                roomId = device.roomId,
                locationId = device.locationId
            )
    }
}
