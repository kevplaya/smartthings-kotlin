package com.example.smartthings.application.service

import com.example.smartthings.domain.model.Device
import com.example.smartthings.domain.port.out.LoadDevicesPort
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeoutException

class DeviceServiceTest {

    private lateinit var loadDevicesPort: LoadDevicesPort
    private lateinit var service: DeviceService

    @BeforeEach
    fun setup() {
        loadDevicesPort = mockk()
        val registry = CircuitBreakerRegistry.of(CircuitBreakerConfig.ofDefaults())
        service = DeviceService(loadDevicesPort, registry)
    }

    @Test
    fun `getDevices should return device list from port`() = runBlocking {
        val mockDevices = listOf(
            Device(
                deviceId = "device-1",
                name = "Living Room Light",
                label = "Living Room Light",
                manufacturerCode = "samsung",
                typeName = "Light",
                type = "LIGHT",
                roomId = "room-1",
                locationId = "location-1"
            ),
            Device(
                deviceId = "device-2",
                name = "Kitchen Switch",
                label = "Kitchen Switch",
                manufacturerCode = "samsung",
                typeName = "Switch",
                type = "SWITCH",
                roomId = "room-2",
                locationId = "location-1"
            )
        )
        coEvery { loadDevicesPort.loadDevices() } returns mockDevices

        val result = service.getDevices()

        assertThat(result).hasSize(2)
        assertThat(result[0].deviceId).isEqualTo("device-1")
        assertThat(result[0].name).isEqualTo("Living Room Light")
        assertThat(result[1].deviceId).isEqualTo("device-2")
        assertThat(result[1].name).isEqualTo("Kitchen Switch")
        coVerify(exactly = 1) { loadDevicesPort.loadDevices() }
    }

    @Test
    fun `getDevices should return empty list when port returns empty`() = runBlocking {
        coEvery { loadDevicesPort.loadDevices() } returns emptyList()

        val result = service.getDevices()

        assertThat(result).isEmpty()
        coVerify(exactly = 1) { loadDevicesPort.loadDevices() }
    }

    @Test
    fun `getDevices should propagate WebClientResponseException when upstream returns 4xx`() = runBlocking {
        val ex = WebClientResponseException.create(
            404,
            "Not Found",
            HttpHeaders.EMPTY,
            byteArrayOf(),
            StandardCharsets.UTF_8
        )
        coEvery { loadDevicesPort.loadDevices() } throws ex

        assertThatThrownBy { runBlocking { service.getDevices() } }
            .isInstanceOf(WebClientResponseException::class.java)
            .hasMessageContaining("404")
        coVerify(exactly = 1) { loadDevicesPort.loadDevices() }
    }

    @Test
    fun `getDevices should propagate WebClientResponseException when upstream returns 5xx`() = runBlocking {
        val ex = WebClientResponseException.create(
            503,
            "Service Unavailable",
            HttpHeaders.EMPTY,
            byteArrayOf(),
            StandardCharsets.UTF_8
        )
        coEvery { loadDevicesPort.loadDevices() } throws ex

        assertThatThrownBy { runBlocking { service.getDevices() } }
            .isInstanceOf(WebClientResponseException::class.java)
            .hasMessageContaining("503")
        coVerify(exactly = 1) { loadDevicesPort.loadDevices() }
    }

    @Test
    fun `getDevices should propagate TimeoutException when upstream times out`() = runBlocking {
        coEvery { loadDevicesPort.loadDevices() } throws TimeoutException("Read timed out")

        assertThatThrownBy { runBlocking { service.getDevices() } }
            .isInstanceOf(TimeoutException::class.java)
            .hasMessageContaining("Read timed out")
        coVerify(exactly = 1) { loadDevicesPort.loadDevices() }
    }
}
