package com.example.smartthings.client

import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThat

class SmartThingsClientTest {

    private lateinit var client: SmartThingsClient
    private lateinit var webClient: WebClient

    @BeforeEach
    fun setup() {
        webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build()
        client = SmartThingsClient(webClient, "test-token")
    }

    @Test
    fun `client should be initialized`() {
        assertThat(client).isNotNull
    }
}
