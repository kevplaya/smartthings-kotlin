package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DeviceAliasRequest(
    @JsonProperty("alias")
    val alias: String
)

data class DeviceAliasResponse(
    @JsonProperty("alias")
    val alias: String
)
