package com.furrowb.scheduler.controller

import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.service.ReservationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/v1/availability")
class AvailabilityController(@Autowired private val reservationService: ReservationService) {

    @PostMapping("/reserve")
    fun reserve(@RequestBody @Validated request: ReservationRequest): Boolean {
        reservationService.scheduleReservation(request)
        return true
    }
}