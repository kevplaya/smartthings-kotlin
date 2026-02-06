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

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE - 1
}
