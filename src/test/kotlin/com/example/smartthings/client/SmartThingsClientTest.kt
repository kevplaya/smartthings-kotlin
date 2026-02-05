package com.example.smartthings.client

import com.example.smartthings.service.SmartThingsTokenProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.web.reactive.function.client.WebClient

class SmartThingsClientTest {

    private lateinit var client: SmartThingsClient
    private lateinit var webClient: WebClient
    private lateinit var tokenProvider: SmartThingsTokenProvider

    @BeforeEach
    fun setup() {
        webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build()
        tokenProvider = mockk()
        every { tokenProvider.getToken() } returns "test-token"
        client = SmartThingsClient(webClient, tokenProvider)
    }

    @Test
    fun `client should be initialized`() {
        assertThat(client).isNotNull
    }
}
