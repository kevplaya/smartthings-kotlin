package com.example.smartthings.adapter.out.smartthings.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SmartThingsDevicesResponse(
    val items: List<SmartThingsDeviceResponse> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SmartThingsDeviceResponse(
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
