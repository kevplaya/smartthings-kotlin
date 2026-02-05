package com.example.smartthings.web

import com.example.smartthings.service.OAuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpCookie
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/oauth")
class OAuthController(
    private val oauthService: OAuthService,
    @Value("\${server.frontend-url}") private val frontendUrl: String
) {
    companion object {
        private const val SESSION_COOKIE_NAME = "SESSION_ID"
        private const val COOKIE_MAX_AGE = 86400 * 7
    }

    @GetMapping("/authorize")
    suspend fun getAuthorizeUrl(): Map<String, String> {
        val state = UUID.randomUUID().toString()
        val url = oauthService.getAuthorizationUrl(state)
        return mapOf("url" to url)
    }

    @GetMapping("/callback")
    suspend fun callback(
        @RequestParam code: String,
        exchange: ServerWebExchange
    ): ResponseEntity<Void> {
        val session = oauthService.exchangeCodeForToken(code)
        val cookie = ResponseCookie.from(SESSION_COOKIE_NAME, session.id)
            .path("/")
            .maxAge(COOKIE_MAX_AGE.toLong())
            .httpOnly(true)
            .sameSite("Lax")
            .secure(frontendUrl.startsWith("https"))
            .build()
        exchange.response.addCookie(cookie)
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("$frontendUrl/oauth/callback"))
            .build()
    }

    @PostMapping("/logout")
    suspend fun logout(
        @CookieValue(SESSION_COOKIE_NAME) sessionId: String?
    ): ResponseEntity<Void> {
        oauthService.deleteSession(sessionId)
        val cookie = ResponseCookie.from(SESSION_COOKIE_NAME, "")
            .path("/")
            .maxAge(0)
            .httpOnly(true)
            .sameSite("Lax")
            .build()
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(frontendUrl))
            .header("Set-Cookie", cookie.toString())
            .build()
    }
}
