package com.example.smartthings.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "installed_app")
data class InstalledApp(
    @Id
    @Column(name = "installed_app_id", nullable = false)
    val installedAppId: String,

    @Column(name = "auth_token", nullable = false, length = 2048)
    var authToken: String,

    @Column(name = "refresh_token", nullable = false, length = 2048)
    var refreshToken: String,

    @Column(name = "location_id", nullable = false)
    val locationId: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
