package com.example.smartthings.web

import com.example.smartthings.service.DeviceAliasService
import com.example.smartthings.web.dto.DeviceAliasRequest
import com.example.smartthings.web.dto.DeviceAliasResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users/{userId}/devices/{deviceId}")
class DeviceAliasController(
    private val deviceAliasService: DeviceAliasService
) {
    @GetMapping("/alias")
    suspend fun getAlias(
        @PathVariable userId: String,
        @PathVariable deviceId: String
    ): DeviceAliasResponse {
        val alias = deviceAliasService.getAlias(userId, deviceId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No alias found for device")
        return DeviceAliasResponse(alias = alias)
    }

    @PutMapping("/alias")
    suspend fun setAlias(
        @PathVariable userId: String,
        @PathVariable deviceId: String,
        @RequestBody request: DeviceAliasRequest
    ): DeviceAliasResponse {
        val alias = deviceAliasService.setAlias(userId, deviceId, request.alias)
        return DeviceAliasResponse(alias = alias)
    }
}
