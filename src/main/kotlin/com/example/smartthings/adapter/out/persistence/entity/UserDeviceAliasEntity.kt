package com.example.smartthings.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(
    name = "user_device_alias",
    uniqueConstraints = [jakarta.persistence.UniqueConstraint(columnNames = ["user_id", "device_id"])]
)
data class UserDeviceAliasEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "device_id", nullable = false)
    val deviceId: String,

    @Column(nullable = false)
    var alias: String,

    @Version
    var version: Long = 0L
)
