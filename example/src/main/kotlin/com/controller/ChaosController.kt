package com.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chaos")
class ChaosController {

    @GetMapping("/delay")
    fun delay(@RequestParam(defaultValue = "1000") ms: Long): ResponseEntity<ChaosResponse> {
        Thread.sleep(ms)
        return ResponseEntity.ok(
            ChaosResponse(
                action = "delay",
                parameter = "${ms}ms",
                message = "Delayed for ${ms}ms"
            )
        )
    }

    @GetMapping("/error")
    fun error(@RequestParam(defaultValue = "500") code: Int): ResponseEntity<ChaosResponse> {
        val status = try {
            HttpStatus.valueOf(code)
        } catch (e: IllegalArgumentException) {
            HttpStatus.INTERNAL_SERVER_ERROR
        }

        return ResponseEntity
            .status(status)
            .body(
                ChaosResponse(
                    action = "error",
                    parameter = code.toString(),
                    message = "Returning HTTP $code"
                )
            )
    }
}

data class ChaosResponse(
    val action: String,
    val parameter: String,
    val message: String
)
