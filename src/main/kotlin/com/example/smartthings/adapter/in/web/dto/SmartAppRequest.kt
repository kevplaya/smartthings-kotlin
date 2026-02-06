package com.example.smartthings.adapter.`in`.web.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SmartAppRequest(
    val lifecycle: String? = null,
    val confirmationData: ConfirmationData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfirmationData(
    val appId: String? = null,
    val confirmationUrl: String? = null,
)
