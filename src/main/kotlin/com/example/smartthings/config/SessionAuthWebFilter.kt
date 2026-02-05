package com.example.smartthings.config

import com.example.smartthings.domain.UserSession
import com.example.smartthings.service.OAuthService
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class SessionAuthWebFilter(
    private val oauthService: OAuthService
) : WebFilter, Ordered {

    companion object {
        const val SESSION_ATTRIBUTE = "userSession"
        private val PUBLIC_PATHS = setOf(
            "/api/oauth/authorize",
            "/api/oauth/callback",
            "/api/user/me",
            "/smartapp"
        )
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        val method = exchange.request.method.name()
        
        // SmartThings webhook POST 요청은 인증 제외
        if (method == "POST" && path == "/") {
            return chain.filter(exchange)
        }
        
        if (PUBLIC_PATHS.any { path.startsWith(it) }) {
            return chain.filter(exchange)
        }
        val sessionId = exchange.request.cookies.getFirst("SESSION_ID")?.value
        val session = oauthService.getSession(sessionId)
        when {
            session == null -> {
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return exchange.response.setComplete()
            }
            session.expiresAt.isBefore(Instant.now()) -> {
                val refreshed = oauthService.refreshAccessToken(session)
                if (refreshed != null) {
                    exchange.attributes[SESSION_ATTRIBUTE] = refreshed
                    return chain.filter(exchange)
                }
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return exchange.response.setComplete()
            }
            else -> {
                exchange.attributes[SESSION_ATTRIBUTE] = session
                return chain.filter(exchange)
            }
        }
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 10
}
