package com.example.smartthings.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SmartThingsConfig {

    @Bean
    fun smartThingsWebClient(
        @Value("\${smartthings.api.base-url}") baseUrl: String,
        @Value("\${smartthings.api.token}") token: String
    ): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .build()
    }
}
