package com.furrowb.scheduler.controller

import com.furrowb.scheduler.model.ReserveRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/availability")
class AvailabilityController {

    @PostMapping("/reserve")
    fun reserve(@RequestBody @Validated  request: ReserveRequest): Boolean {
        return true
    }
}