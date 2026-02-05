package com.example.smartthings.web

import com.example.smartthings.web.dto.ErrorResponse
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.Exceptions
import java.util.concurrent.TimeoutException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(org.springframework.web.reactive.function.client.WebClientResponseException::class)
    fun handleWebClientResponseException(
        ex: org.springframework.web.reactive.function.client.WebClientResponseException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val status = ex.statusCode
        val upstream = status.value()
        val gatewayStatus = if (status.is4xxClientError) HttpStatus.BAD_GATEWAY else HttpStatus.SERVICE_UNAVAILABLE
        logger.error("Upstream API error: {} {} - path={}", upstream, ex.message, exchange.request.path.value(), ex)
        val body = ErrorResponse(
            code = gatewayStatus.value(),
            message = "Upstream API error: ${ex.message ?: status.toString()}",
            path = exchange.request.path.value()
        )
        return ResponseEntity.status(gatewayStatus).body(body)
    }

    @ExceptionHandler(CallNotPermittedException::class)
    fun handleCallNotPermittedException(
        ex: CallNotPermittedException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        logger.error("Circuit breaker open - path={}", exchange.request.path.value(), ex)
        val body = ErrorResponse(
            code = HttpStatus.SERVICE_UNAVAILABLE.value(),
            message = "SmartThings API temporarily unavailable",
            path = exchange.request.path.value()
        )
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body)
    }

    @ExceptionHandler(TimeoutException::class)
    fun handleTimeoutException(
        ex: TimeoutException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        logger.error("Request timeout - path={}", exchange.request.path.value(), ex)
        val body = ErrorResponse(
            code = HttpStatus.GATEWAY_TIMEOUT.value(),
            message = "Request timeout: ${ex.message ?: "Upstream did not respond in time"}",
            path = exchange.request.path.value()
        )
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        val cause = Exceptions.unwrap(ex)
        if (cause is TimeoutException) {
            return handleTimeoutException(cause, exchange)
        }
        logger.error("Unexpected error - path={}", exchange.request.path.value(), ex)
        val body = ErrorResponse(
            code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "Internal server error: ${ex.message ?: "Unknown error"}",
            path = exchange.request.path.value()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
