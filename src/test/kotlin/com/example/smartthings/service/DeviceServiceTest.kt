package com.example.smartthings.service

import com.example.smartthings.client.SmartThingsClient
import com.example.smartthings.web.dto.DeviceResponse
import com.example.smartthings.web.dto.DevicesResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeviceServiceTest {

    private lateinit var client: SmartThingsClient
    private lateinit var service: DeviceService

    @BeforeEach
    fun setup() {
        client = mockk()
        service = DeviceService(client)
    }

    @Test
    fun `getDevices should return device list from client`() {
        // given
        val mockDevices = listOf(
            DeviceResponse(
                deviceId = "device-1",
                name = "Living Room Light",
                label = "Living Room Light",
                manufacturerCode = "samsung",
                typeName = "Light",
                type = "LIGHT",
                roomId = "room-1",
                locationId = "location-1"
            ),
            DeviceResponse(
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
        every { client.getDevices() } returns DevicesResponse(mockDevices)

        // when
        val result = service.getDevices()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].deviceId).isEqualTo("device-1")
        assertThat(result[0].name).isEqualTo("Living Room Light")
        assertThat(result[1].deviceId).isEqualTo("device-2")
        assertThat(result[1].name).isEqualTo("Kitchen Switch")
        verify(exactly = 1) { client.getDevices() }
    }

    @Test
    fun `getDevices should return empty list when client returns empty`() {
        // given
        every { client.getDevices() } returns DevicesResponse(emptyList())

        // when
        val result = service.getDevices()

        // then
        assertThat(result).isEmpty()
        verify(exactly = 1) { client.getDevices() }
    }
}
