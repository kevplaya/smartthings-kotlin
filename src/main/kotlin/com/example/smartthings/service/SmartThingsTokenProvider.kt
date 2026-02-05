package com.example.smartthings.service

import com.example.smartthings.repository.InstalledAppRepository
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(SmartThingsTokenProvider::class.java)

    fun getToken(): String {
        val installedAppToken = installedAppRepository.findFirstByOrderByCreatedAtAsc()?.authToken

        return when {
            !installedAppToken.isNullOrBlank() -> {
                logger.debug("Using InstalledApp token")
                installedAppToken
            }
            !pat.isNullOrBlank() -> {
                logger.debug("Using PAT token")
                pat
            }
            else -> {
                logger.error("No valid token available (neither InstalledApp nor PAT)")
                throw IllegalStateException("SmartThings token not configured")
            }
        }
    }
}
