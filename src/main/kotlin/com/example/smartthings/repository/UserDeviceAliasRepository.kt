package com.example.smartthings.repository

import com.example.smartthings.domain.UserDeviceAlias
import org.springframework.data.jpa.repository.JpaRepository

interface UserDeviceAliasRepository : JpaRepository<UserDeviceAlias, Long> {

    fun findByUserIdAndDeviceId(userId: String, deviceId: String): UserDeviceAlias?

    fun existsByUserIdAndDeviceId(userId: String, deviceId: String): Boolean
}
