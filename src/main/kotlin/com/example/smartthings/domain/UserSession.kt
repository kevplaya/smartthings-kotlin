package com.example.smartthings.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "user_session")
data class UserSession(
    @Id
    @Column(name = "id", length = 36, nullable = false)
    val id: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "access_token", nullable = false, length = 1024)
    var accessToken: String,

    @Column(name = "refresh_token", nullable = false, length = 1024)
    var refreshToken: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
