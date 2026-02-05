package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val code: Int,
    val message: String,
    val path: String? = null,
    val timestamp: Instant = Instant.now(),
    val errors: List<String>? = null
)
