package com.nhn.inje.ccp.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/healthz")
    fun healthz(): ResponseEntity<HealthResponse> {
        return ResponseEntity.ok(
            HealthResponse(
                status = "UP",
                liveness = true,
                readiness = true
            )
        )
    }
}

data class HealthResponse(
    val status: String,
    val liveness: Boolean,
    val readiness: Boolean
)
