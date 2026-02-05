package com.example.smartthings.service

import com.example.smartthings.domain.UserSession
import com.example.smartthings.web.dto.OAuthTokenResponse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID

@Service
class OAuthService(
    private val userSessionRepository: com.example.smartthings.repository.UserSessionRepository,
    @Value("\${smartthings.oauth.client-id}") private val clientId: String,
    @Value("\${smartthings.oauth.client-secret}") private val clientSecret: String,
    @Value("\${smartthings.oauth.authorization-uri}") private val authorizationUri: String,
    @Value("\${smartthings.oauth.token-uri}") private val tokenUri: String,
    @Value("\${smartthings.oauth.redirect-uri}") private val redirectUri: String,
    @Value("\${smartthings.oauth.scopes}") private val scopes: String,
    @Value("\${smartthings.api.base-url}") private val apiBaseUrl: String
) {
    private val logger = LoggerFactory.getLogger(OAuthService::class.java)
    private val oauthWebClient: WebClient = WebClient.builder().build()

    fun getAuthorizationUrl(state: String): String {
        val scopeEncoded = URLEncoder.encode(scopes, StandardCharsets.UTF_8)
        val redirectEncoded = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
        val stateEncoded = URLEncoder.encode(state, StandardCharsets.UTF_8)
        return "$authorizationUri?client_id=$clientId&scope=$scopeEncoded&redirect_uri=$redirectEncoded&response_type=code&state=$stateEncoded"
    }

    suspend fun exchangeCodeForToken(code: String): UserSession {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("code", code)
            add("grant_type", "authorization_code")
            add("redirect_uri", redirectUri)
        }
        val tokenResponse = oauthWebClient.post()
            .uri(tokenUri)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .bodyValue(body)
            .retrieve()
            .bodyToMono<OAuthTokenResponse>()
            .doOnError { logger.error("Token exchange failed", it) }
            .awaitSingle()

        val expiresAt = Instant.now().plusSeconds(tokenResponse.expiresIn.toLong())
        val userId = fetchUserId(tokenResponse.accessToken)
        val sessionId = UUID.randomUUID().toString()

        val session = UserSession(
            id = sessionId,
            userId = userId,
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            expiresAt = expiresAt
        )
        return userSessionRepository.save(session)
    }

    private suspend fun fetchUserId(accessToken: String): String {
        return try {
            val userWebClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .build()
            val map = userWebClient.get()
                .uri("/user")
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .awaitSingle()
            (map["id"] as? String) ?: "me"
        } catch (e: Exception) {
            logger.warn("Could not fetch user id, using 'me'", e)
            "me"
        }
    }

    fun refreshAccessToken(session: UserSession): UserSession? = runBlocking {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("grant_type", "refresh_token")
            add("refresh_token", session.refreshToken)
        }
        try {
            val tokenResponse = oauthWebClient.post()
                .uri(tokenUri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono<OAuthTokenResponse>()
                .awaitSingle()
            val expiresAt = Instant.now().plusSeconds(tokenResponse.expiresIn.toLong())
            session.accessToken = tokenResponse.accessToken
            session.refreshToken = tokenResponse.refreshToken
            session.expiresAt = expiresAt
            userSessionRepository.save(session)
        } catch (e: Exception) {
            logger.error("Refresh token failed", e)
            null
        }
    }

    fun getSession(sessionId: String?): UserSession? {
        if (sessionId.isNullOrBlank()) return null
        return userSessionRepository.findById(sessionId).orElse(null)
    }

    fun deleteSession(sessionId: String?) {
        if (!sessionId.isNullOrBlank()) userSessionRepository.deleteById(sessionId)
    }
}
