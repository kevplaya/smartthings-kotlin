package com.example.smartthings.domain.exception

class DeviceAliasNotFoundException(
    userId: String,
    deviceId: String,
    message: String = "No alias found for device: userId=$userId, deviceId=$deviceId"
) : RuntimeException(message)
