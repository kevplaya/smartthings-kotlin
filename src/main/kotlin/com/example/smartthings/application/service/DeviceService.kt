package com.example.smartthings.application.service

import com.example.smartthings.domain.model.Device
import com.example.smartthings.domain.port.`in`.GetDevicesUseCase
import com.example.smartthings.domain.port.out.LoadDevicesPort
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val loadDevicesPort: LoadDevicesPort,
    circuitBreakerRegistry: CircuitBreakerRegistry
) : GetDevicesUseCase {

    private val circuitBreaker: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("smartthings")

    override suspend fun getDevices(): List<Device> =
        withContext(Dispatchers.IO) {
            circuitBreaker.executeSupplier { runBlocking { loadDevicesPort.loadDevices() } }
        }
}
