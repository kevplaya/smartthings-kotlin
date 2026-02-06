package com.example.smartthings.adapter.out.persistence.repository

import com.example.smartthings.adapter.out.persistence.entity.UserDeviceAliasEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserDeviceAliasJpaRepository : JpaRepository<UserDeviceAliasEntity, Long> {

    fun findByUserIdAndDeviceId(userId: String, deviceId: String): UserDeviceAliasEntity?

    fun existsByUserIdAndDeviceId(userId: String, deviceId: String): Boolean
}
