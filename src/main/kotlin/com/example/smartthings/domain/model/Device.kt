package com.example.smartthings.domain.model

data class Device(
    val deviceId: String,
    val name: String,
    val label: String,
    val manufacturerCode: String? = null,
    val typeName: String? = null,
    val type: String? = null,
    val roomId: String? = null,
    val locationId: String? = null
)
