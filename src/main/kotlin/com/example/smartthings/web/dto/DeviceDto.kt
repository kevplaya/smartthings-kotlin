package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DevicesResponse(
    val items: List<DeviceResponse> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeviceResponse(
    @JsonProperty("deviceId")
    val deviceId: String,
    
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("label")
    val label: String,
    
    @JsonProperty("deviceManufacturerCode")
    val manufacturerCode: String? = null,
    
    @JsonProperty("deviceTypeName")
    val typeName: String? = null,
    
    @JsonProperty("type")
    val type: String? = null,
    
    @JsonProperty("roomId")
    val roomId: String? = null,
    
    @JsonProperty("locationId")
    val locationId: String? = null
)

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
        fun from(response: DeviceResponse): DeviceDto {
            return DeviceDto(
                deviceId = response.deviceId,
                name = response.name,
                label = response.label,
                manufacturerCode = response.manufacturerCode,
                typeName = response.typeName,
                type = response.type,
                roomId = response.roomId,
                locationId = response.locationId
            )
        }
    }
}
