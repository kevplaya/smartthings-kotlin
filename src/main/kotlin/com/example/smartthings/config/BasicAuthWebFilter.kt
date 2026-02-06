package com.example.smartthings.config

import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.Base64

private val ALLOWED_ORIGINS = setOf(
    "http://localhost:5173",
    "https://smartthings-kotlin.vercel.app",
)

private fun isAllowedOrigin(origin: String?): Boolean {
    if (origin == null) return false
    if (origin in ALLOWED_ORIGINS) return true
    return origin.endsWith(".vercel.app") && origin.startsWith("https://")
}

@Component
class BasicAuthWebFilter(
    @org.springframework.beans.factory.annotation.Value("\${basic.auth.username}") private val username: String,
    @org.springframework.beans.factory.annotation.Value("\${basic.auth.password}") private val password: String,
) : WebFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        val method = exchange.request.method.name()

        if (method == "OPTIONS") {
            return chain.filter(exchange)
        }

        if (method == "POST" && path == "/") {
            return chain.filter(exchange)
        }

        if (path.startsWith("/api/")) {
            val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            if (authHeader == null || !isValidAuth(authHeader)) {
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.headers.add(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"SmartThings\"")
                val origin = exchange.request.headers.getFirst(HttpHeaders.ORIGIN)
                if (isAllowedOrigin(origin)) {
                    exchange.response.headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin!!)
                    exchange.response.headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                }
                return exchange.response.setComplete()
            }
        }

        return chain.filter(exchange)
    }

    private fun isValidAuth(authHeader: String): Boolean {
        if (!authHeader.startsWith("Basic ")) return false
        val base64Credentials = authHeader.substring(6)
        val credentials = String(Base64.getDecoder().decode(base64Credentials))
        val parts = credentials.split(":", limit = 2)
        return parts.size == 2 && parts[0] == username && parts[1] == password
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 10
}
