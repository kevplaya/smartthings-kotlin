package com.example.smartthings.domain.port.`in`

interface GetDeviceAliasQuery {
    suspend fun getAlias(userId: String, deviceId: String): String?
}
