package com.example.smartthings.web

import com.example.smartthings.web.dto.SmartAppRequest
import com.example.smartthings.web.dto.SmartAppResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/smartapp")
class SmartAppController(
    private val webClient: WebClient.Builder,
    @Value("\${smartthings.webhook.target-url}") private val targetUrl: String,
) {
    private val logger = LoggerFactory.getLogger(SmartAppController::class.java)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun handleLifecycle(@RequestBody request: SmartAppRequest): SmartAppResponse = withContext(Dispatchers.IO) {
        val lifecycle = request.lifecycle ?: when {
            request.confirmationData != null -> "CONFIRMATION"
            else -> {
                logger.warn("Unknown request type: {}", request)
                return@withContext SmartAppResponse()
            }
        }

        logger.info("SmartApp {} request", lifecycle)
        when (lifecycle) {
            "CONFIRMATION" -> handleConfirmation(request)
            else -> {
                logger.warn("Unhandled lifecycle: {}", lifecycle)
                SmartAppResponse()
            }
        }
    }

    private suspend fun handleConfirmation(request: SmartAppRequest): SmartAppResponse {
        val confirmationUrl = request.confirmationData?.confirmationUrl
        if (!confirmationUrl.isNullOrBlank()) {
            try {
                webClient.build()
                    .get()
                    .uri(confirmationUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toBodilessEntity()
                    .awaitSingle()
                logger.info("Confirmation URL called successfully: {}", confirmationUrl)
            } catch (e: Exception) {
                logger.error("Failed to call confirmation URL: {}", confirmationUrl, e)
            }
        }
        return SmartAppResponse(targetUrl = targetUrl)
    }
}
