package com.example.smartthings.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val config = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOrigin("http://localhost:5173")
            addAllowedOriginPattern("https://*.vercel.app")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", config)
        }
        return CorsWebFilter(source)
    }
}
