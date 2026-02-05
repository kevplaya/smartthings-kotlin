package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SmartAppResponse(
    val targetUrl: String? = null,
)
