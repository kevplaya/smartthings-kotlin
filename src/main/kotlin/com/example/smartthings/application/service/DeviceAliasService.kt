package com.example.smartthings.application.service

import com.example.smartthings.domain.model.DeviceAlias
import com.example.smartthings.domain.port.`in`.GetDeviceAliasQuery
import com.example.smartthings.domain.port.`in`.SetDeviceAliasCommand
import com.example.smartthings.domain.port.out.LoadDeviceAliasPort
import com.example.smartthings.domain.port.out.SaveDeviceAliasPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeviceAliasService(
    private val loadDeviceAliasPort: LoadDeviceAliasPort,
    private val saveDeviceAliasPort: SaveDeviceAliasPort
) : GetDeviceAliasQuery, SetDeviceAliasCommand {

    override suspend fun getAlias(userId: String, deviceId: String): String? =
        withContext(Dispatchers.IO) {
            loadDeviceAliasPort.loadByUserIdAndDeviceId(userId, deviceId)?.alias
        }

    override suspend fun setAlias(userId: String, deviceId: String, alias: String): String =
        withContext(Dispatchers.IO) {
            val existing = loadDeviceAliasPort.loadByUserIdAndDeviceId(userId, deviceId)
            val toSave = existing?.copy(alias = alias)
                ?: DeviceAlias(userId = userId, deviceId = deviceId, alias = alias)
            saveDeviceAliasPort.save(toSave).alias
        }
}
