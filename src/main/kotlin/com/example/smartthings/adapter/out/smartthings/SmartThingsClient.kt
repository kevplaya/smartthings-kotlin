package com.example.smartthings.adapter.out.smartthings

import com.example.smartthings.adapter.out.smartthings.dto.SmartThingsDevicesResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SmartThingsClient(
    private val smartThingsWebClient: WebClient,
    @Value("\${smartthings.api.token}") private val token: String
) {
    private val logger = LoggerFactory.getLogger(SmartThingsClient::class.java)

    suspend fun fetchDevices(): SmartThingsDevicesResponse {
        logger.info("Fetching devices from SmartThings API")
        return smartThingsWebClient
            .get()
            .uri("/devices")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono(SmartThingsDevicesResponse::class.java)
            .doOnSuccess { it?.let { resp -> logger.info("Successfully fetched ${resp.items.size} devices") } }
            .doOnError { logger.error("Failed to fetch devices", it) }
            .awaitSingle()
    }
}
