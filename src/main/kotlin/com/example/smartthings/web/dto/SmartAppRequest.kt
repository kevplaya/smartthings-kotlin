package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SmartAppRequest(
    val lifecycle: String? = null,
    val confirmationData: ConfirmationData? = null,
    val configurationData: ConfigurationRequestData? = null,
    val installData: InstallData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfirmationData(
    val appId: String? = null,
    @JsonProperty("confirmationUrl") val confirmationUrl: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfigurationRequestData(
    val installedAppId: String? = null,
    val phase: String? = null,
    val pageId: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstallData(
    val authToken: String? = null,
    val refreshToken: String? = null,
    val installedApp: InstalledAppData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstalledAppData(
    @JsonProperty("installedAppId") val installedAppId: String? = null,
    val locationId: String? = null,
    val permissions: List<String>? = null,
)
