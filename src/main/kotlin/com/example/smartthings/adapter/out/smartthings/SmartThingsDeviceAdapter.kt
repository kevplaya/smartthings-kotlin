package com.example.smartthings.adapter.out.smartthings

import com.example.smartthings.adapter.out.smartthings.dto.SmartThingsDeviceResponse
import com.example.smartthings.domain.model.Device
import com.example.smartthings.domain.port.out.LoadDevicesPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class SmartThingsDeviceAdapter(
    private val smartThingsClient: SmartThingsClient
) : LoadDevicesPort {

    override suspend fun loadDevices(): List<Device> = withContext(Dispatchers.IO) {
        smartThingsClient.fetchDevices().items.map { it.toDomain() }
    }
}

private fun SmartThingsDeviceResponse.toDomain(): Device =
    Device(
        deviceId = deviceId,
        name = name,
        label = label,
        manufacturerCode = manufacturerCode,
        typeName = typeName,
        type = type,
        roomId = roomId,
        locationId = locationId
    )
