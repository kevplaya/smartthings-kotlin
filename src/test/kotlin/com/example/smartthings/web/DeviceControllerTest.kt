package com.example.smartthings.web

import com.example.smartthings.config.SessionAuthWebFilter
import com.example.smartthings.domain.UserSession
import com.example.smartthings.service.DeviceService
import com.example.smartthings.web.dto.DeviceDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ServerWebExchange

class DeviceControllerTest {

    private lateinit var deviceService: DeviceService
    private lateinit var controller: DeviceController

    @BeforeEach
    fun setup() {
        deviceService = mockk()
        controller = DeviceController(deviceService)
    }

    private fun mockExchangeWithSession(accessToken: String): ServerWebExchange {
        val exchange = mockk<ServerWebExchange>()
        val session = UserSession(
            id = "session-1",
            userId = "user-1",
            accessToken = accessToken,
            refreshToken = "refresh-1",
            expiresAt = java.time.Instant.now().plusSeconds(3600)
        )
        every { exchange.getAttribute<UserSession>(SessionAuthWebFilter.SESSION_ATTRIBUTE) } returns session
        return exchange
    }

    @Test
    fun `getDevices should return device list from service`() = runBlocking {
        // given
        val mockDevices = listOf(
            DeviceDto(
                deviceId = "device-1",
                name = "Living Room Light",
                label = "Living Room Light",
                manufacturerCode = "samsung",
                typeName = "Light",
                type = "LIGHT",
                roomId = "room-1",
                locationId = "location-1"
            )
        )
        coEvery { deviceService.getDevices("token-1") } returns mockDevices
        val exchange = mockExchangeWithSession("token-1")

        // when
        val result = controller.getDevices(exchange)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].deviceId).isEqualTo("device-1")
        coVerify(exactly = 1) { deviceService.getDevices("token-1") }
    }

    @Test
    fun `getDevices should return empty list when service returns empty`() = runBlocking {
        // given
        coEvery { deviceService.getDevices("token-1") } returns emptyList()
        val exchange = mockExchangeWithSession("token-1")

        // when
        val result = controller.getDevices(exchange)

        // then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { deviceService.getDevices("token-1") }
    }
}
