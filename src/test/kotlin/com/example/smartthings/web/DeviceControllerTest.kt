package com.example.smartthings.web

import com.example.smartthings.service.DeviceService
import com.example.smartthings.web.dto.DeviceDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeviceControllerTest {

    private lateinit var deviceService: DeviceService
    private lateinit var controller: DeviceController

    @BeforeEach
    fun setup() {
        deviceService = mockk()
        controller = DeviceController(deviceService)
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
        coEvery { deviceService.getDevices() } returns mockDevices

        // when
        val result = controller.getDevices()

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].deviceId).isEqualTo("device-1")
        coVerify(exactly = 1) { deviceService.getDevices() }
    }

    @Test
    fun `getDevices should return empty list when service returns empty`() = runBlocking {
        // given
        coEvery { deviceService.getDevices() } returns emptyList()

        // when
        val result = controller.getDevices()

        // then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { deviceService.getDevices() }
    }
}
