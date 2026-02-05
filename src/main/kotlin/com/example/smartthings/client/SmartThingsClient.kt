package com.example.smartthings.client

import com.example.smartthings.web.dto.DevicesResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class SmartThingsClient(
    private val smartThingsWebClient: WebClient
) {
    private val logger = LoggerFactory.getLogger(SmartThingsClient::class.java)

    fun getDevices(): DevicesResponse {
        logger.info("Fetching devices from SmartThings API")
        
        val response = smartThingsWebClient
            .get()
            .uri("/devices")
            .retrieve()
            .bodyToMono<DevicesResponse>()
            .doOnSuccess { it?.let { resp -> logger.info("Successfully fetched ${resp.items.size} devices") } }
            .doOnError { logger.error("Failed to fetch devices", it) }
            .block()
        
        return response ?: DevicesResponse(emptyList())
    }
}
