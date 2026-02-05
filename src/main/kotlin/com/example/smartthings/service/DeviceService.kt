package com.example.smartthings.service

import com.example.smartthings.port.DeviceSource
import com.example.smartthings.web.dto.DeviceDto
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val deviceSource: DeviceSource,
    circuitBreakerRegistry: CircuitBreakerRegistry
) {
    private val circuitBreaker: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("smartthings")

    suspend fun getDevices(): List<DeviceDto> = withContext(Dispatchers.IO) {
        val response = circuitBreaker.executeSupplier { runBlocking { deviceSource.getDevices() } }
        response.items.map { DeviceDto.from(it) }
    }
}
