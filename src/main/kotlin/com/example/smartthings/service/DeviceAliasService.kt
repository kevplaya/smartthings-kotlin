package com.example.smartthings.service

import com.example.smartthings.domain.UserDeviceAlias
import com.example.smartthings.repository.UserDeviceAliasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeviceAliasService(
    private val userDeviceAliasRepository: UserDeviceAliasRepository
) {
    suspend fun getAlias(userId: String, deviceId: String): String? = withContext(Dispatchers.IO) {
        userDeviceAliasRepository.findByUserIdAndDeviceId(userId, deviceId)?.alias
    }

    suspend fun setAlias(userId: String, deviceId: String, alias: String): String = withContext(Dispatchers.IO) {
        val existing = userDeviceAliasRepository.findByUserIdAndDeviceId(userId, deviceId)
        val entity = if (existing != null) {
            existing.apply { this.alias = alias }
        } else {
            UserDeviceAlias(userId = userId, deviceId = deviceId, alias = alias)
        }
        userDeviceAliasRepository.save(entity).alias
    }
}
