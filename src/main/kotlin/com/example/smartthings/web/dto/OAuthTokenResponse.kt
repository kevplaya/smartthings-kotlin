package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("refresh_token") val refreshToken: String,
    @JsonProperty("token_type") val tokenType: String = "bearer",
    @JsonProperty("expires_in") val expiresIn: Int
)
