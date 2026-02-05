package com.example.smartthings.service

import com.example.smartthings.repository.InstalledAppRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Provides the token for SmartThings API calls.
 * Uses the first installed app's authToken if available, otherwise the configured PAT.
 */
@Component
class SmartThingsTokenProvider(
    private val installedAppRepository: InstalledAppRepository,
    @Value("\${smartthings.api.token}") private val pat: String,
) {
    fun getToken(): String =
        installedAppRepository.findFirstByOrderByCreatedAtAsc()?.authToken ?: pat
}
