package com.example.smartthings.web

import com.example.smartthings.domain.InstalledApp
import com.example.smartthings.repository.InstalledAppRepository
import com.example.smartthings.web.dto.ConfigurationResponseData
import com.example.smartthings.web.dto.InitializeData
import com.example.smartthings.web.dto.PageData
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
import java.time.Instant

@RestController
@RequestMapping("/smartapp")
class SmartAppController(
    private val installedAppRepository: InstalledAppRepository,
    private val webClient: WebClient.Builder,
    @Value("\${smartthings.webhook.target-url}") private val targetUrl: String,
) {
    private val logger = LoggerFactory.getLogger(SmartAppController::class.java)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun handleLifecycle(@RequestBody request: SmartAppRequest): SmartAppResponse = withContext(Dispatchers.IO) {
        val lifecycle = request.lifecycle ?: when {
            request.confirmationData != null -> "CONFIRMATION"
            request.configurationData != null -> "CONFIGURATION"
            request.installData != null -> "INSTALL"
            else -> {
                logger.warn("Unknown request type: {}", request)
                return@withContext SmartAppResponse()
            }
        }

        logger.info("SmartApp {} request", lifecycle)
        when (lifecycle) {
            "CONFIRMATION" -> handleConfirmation(request)
            "CONFIGURATION" -> handleConfiguration(request)
            "INSTALL" -> handleInstall(request)
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
                logger.info("Confirmation URL called successfully")
            } catch (e: Exception) {
                logger.error("Failed to call confirmation URL", e)
            }
        }
        return SmartAppResponse(targetUrl = targetUrl)
    }

    private fun handleConfiguration(request: SmartAppRequest): SmartAppResponse {
        val configurationData = ConfigurationResponseData(
            initialize = InitializeData(
                name = "SmartThings Kotlin App",
                description = "Device control app",
                permissions = listOf("r:devices:*", "x:devices:*"),
                firstPageId = "1",
            ),
            page = PageData(
                pageId = "1",
                name = "완료",
                complete = true,
                sections = emptyList(),
            ),
        )
        return SmartAppResponse(configurationData = configurationData)
    }

    private fun handleInstall(request: SmartAppRequest): SmartAppResponse {
        val installData = request.installData ?: return SmartAppResponse(installData = emptyMap())
        val installedAppData = installData.installedApp ?: return SmartAppResponse(installData = emptyMap())
        val installedAppId = installedAppData.installedAppId
        val authToken = installData.authToken
        val refreshToken = installData.refreshToken
        val locationId = installedAppData.locationId
        if (!installedAppId.isNullOrBlank() && !authToken.isNullOrBlank() && !refreshToken.isNullOrBlank() && !locationId.isNullOrBlank()) {
            val entity = InstalledApp(
                installedAppId = installedAppId,
                authToken = authToken,
                refreshToken = refreshToken,
                locationId = locationId,
                createdAt = Instant.now(),
            )
            installedAppRepository.save(entity)
            logger.info("Installed app saved: {}", installedAppId)
        }
        return SmartAppResponse(installData = emptyMap())
    }
}
