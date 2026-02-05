package com.example.smartthings.repository

import com.example.smartthings.domain.UserSession
import org.springframework.data.jpa.repository.JpaRepository

interface UserSessionRepository : JpaRepository<UserSession, String> {

    fun findByUserId(userId: String): UserSession?
}
