package com.example.smartthings.client

import com.example.smartthings.port.DeviceSource
import com.example.smartthings.web.dto.DevicesResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class SmartThingsClient(
    private val smartThingsWebClient: WebClient,
    @org.springframework.beans.factory.annotation.Value("\${smartthings.api.token}") private val token: String
) : DeviceSource {
    private val logger = LoggerFactory.getLogger(SmartThingsClient::class.java)

    override suspend fun getDevices(): DevicesResponse {
        logger.info("Fetching devices from SmartThings API")
        return smartThingsWebClient
            .get()
            .uri("/devices")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono<DevicesResponse>()
            .doOnSuccess { it?.let { resp -> logger.info("Successfully fetched ${resp.items.size} devices") } }
            .doOnError { logger.error("Failed to fetch devices", it) }
            .awaitSingle()
    }
}
