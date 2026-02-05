package com.example.smartthings

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SmartThingsApplication

fun main(args: Array<String>) {
    runApplication<SmartThingsApplication>(*args)
}
