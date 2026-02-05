package com.example.smartthings.domain.port.`in`

interface SetDeviceAliasCommand {
    suspend fun setAlias(userId: String, deviceId: String, alias: String): String
}
