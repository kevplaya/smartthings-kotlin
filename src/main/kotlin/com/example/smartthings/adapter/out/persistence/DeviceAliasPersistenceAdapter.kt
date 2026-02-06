package com.example.smartthings.adapter.out.persistence

import com.example.smartthings.adapter.out.persistence.entity.UserDeviceAliasEntity
import com.example.smartthings.adapter.out.persistence.repository.UserDeviceAliasJpaRepository
import com.example.smartthings.domain.model.DeviceAlias
import com.example.smartthings.domain.port.out.LoadDeviceAliasPort
import com.example.smartthings.domain.port.out.SaveDeviceAliasPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class DeviceAliasPersistenceAdapter(
    private val repository: UserDeviceAliasJpaRepository
) : LoadDeviceAliasPort, SaveDeviceAliasPort {

    override suspend fun loadByUserIdAndDeviceId(userId: String, deviceId: String): DeviceAlias? =
        withContext(Dispatchers.IO) {
            repository.findByUserIdAndDeviceId(userId, deviceId)?.toDomain()
        }

    override suspend fun save(deviceAlias: DeviceAlias): DeviceAlias =
        withContext(Dispatchers.IO) {
            val existing = repository.findByUserIdAndDeviceId(deviceAlias.userId, deviceAlias.deviceId)
            val entity = if (existing != null) {
                existing.apply { alias = deviceAlias.alias }
            } else {
                deviceAlias.toEntity()
            }
            repository.save(entity).toDomain()
        }
}

private fun UserDeviceAliasEntity.toDomain(): DeviceAlias =
    DeviceAlias(userId = userId, deviceId = deviceId, alias = alias)

private fun DeviceAlias.toEntity(): UserDeviceAliasEntity =
    UserDeviceAliasEntity(userId = userId, deviceId = deviceId, alias = alias)
