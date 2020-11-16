package com.furrowb.scheduler.controller

import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.service.ReservationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1")
class AvailabilityController(@Autowired private val reservationService: ReservationService) {

    @PostMapping("/reservation")
    fun createReservation(@RequestBody @Validated request: ReservationRequest): ResponseEntity<Reservation> {
        val response = reservationService.scheduleReservation(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/reservation")
    fun checkAvailability() {

    }

    @DeleteMapping("/reservation")
    fun deleteReservation() {

    }
}