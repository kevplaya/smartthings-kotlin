package com.example.smartthings.web

import com.example.smartthings.config.SessionAuthWebFilter
import com.example.smartthings.domain.UserSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/api/user")
class UserController {

    @GetMapping("/me")
    suspend fun me(exchange: ServerWebExchange): Map<String, String> {
        val session = exchange.getAttribute<UserSession>(SessionAuthWebFilter.SESSION_ATTRIBUTE)
        return mapOf("id" to (session?.userId ?: ""))
    }
}
