package com.actuator.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class EchoController {

    @PostMapping("/echo")
    fun echo(
        @RequestBody(required = false) body: String?,
        @RequestHeader headers: HttpHeaders
    ): ResponseEntity<EchoResponse> {
        return ResponseEntity.ok(
            EchoResponse(
                body = body ?: "",
                contentType = headers.contentType?.toString(),
                headers = headers.toSingleValueMap()
            )
        )
    }
}

data class EchoResponse(
    val body: String,
    val contentType: String?,
    val headers: Map<String, String>
)
