package com.example.smartthings.adapter.`in`.web.controller

import com.example.smartthings.adapter.`in`.web.dto.DeviceAliasRequest
import com.example.smartthings.adapter.`in`.web.dto.DeviceAliasResponse
import com.example.smartthings.domain.exception.DeviceAliasNotFoundException
import com.example.smartthings.domain.port.`in`.GetDeviceAliasQuery
import com.example.smartthings.domain.port.`in`.SetDeviceAliasCommand
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/{userId}/devices/{deviceId}")
class DeviceAliasController(
    private val getDeviceAliasQuery: GetDeviceAliasQuery,
    private val setDeviceAliasCommand: SetDeviceAliasCommand
) {
    @GetMapping("/alias")
    suspend fun getAlias(
        @PathVariable userId: String,
        @PathVariable deviceId: String
    ): DeviceAliasResponse {
        val alias = getDeviceAliasQuery.getAlias(userId, deviceId)
            ?: throw DeviceAliasNotFoundException(userId, deviceId)
        return DeviceAliasResponse(alias = alias)
    }

    @PutMapping("/alias")
    suspend fun setAlias(
        @PathVariable userId: String,
        @PathVariable deviceId: String,
        @RequestBody request: DeviceAliasRequest
    ): DeviceAliasResponse {
        val alias = setDeviceAliasCommand.setAlias(userId, deviceId, request.alias)
        return DeviceAliasResponse(alias = alias)
    }
}
