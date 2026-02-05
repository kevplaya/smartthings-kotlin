package com.example.smartthings.web.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SmartAppResponse(
    val targetUrl: String? = null,
    val configurationData: ConfigurationResponseData? = null,
    val installData: Map<String, Any?>? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfigurationResponseData(
    val initialize: InitializeData? = null,
    val page: PageData? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class InitializeData(
    val name: String,
    val description: String,
    val permissions: List<String>,
    val firstPageId: String,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PageData(
    val pageId: String,
    val name: String,
    val complete: Boolean,
    val sections: List<SectionData> = emptyList(),
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SectionData(
    val name: String? = null,
    val settings: List<SettingData> = emptyList(),
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SettingData(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
)
