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

    private lateinit var getDevicesUseCase: DeviceService
    private lateinit var controller: DeviceController

    @BeforeEach
    fun setup() {
        getDevicesUseCase = mockk()
        controller = DeviceController(getDevicesUseCase)
    }

    @Test
    fun `getDevices should return device list from service`() = runBlocking {
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
        coEvery { getDevicesUseCase.getDevices() } returns mockDevices

        val result = controller.getDevices()

        assertThat(result).hasSize(1)
        assertThat(result[0].deviceId).isEqualTo("device-1")
        coVerify(exactly = 1) { getDevicesUseCase.getDevices() }
    }

    @Test
    fun `getDevices should return empty list when service returns empty`() = runBlocking {
        coEvery { getDevicesUseCase.getDevices() } returns emptyList()

        val result = controller.getDevices()

        assertThat(result).isEmpty()
        coVerify(exactly = 1) { getDevicesUseCase.getDevices() }
    }
}
