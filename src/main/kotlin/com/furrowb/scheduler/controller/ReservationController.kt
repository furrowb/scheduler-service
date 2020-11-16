package com.furrowb.scheduler.controller

import com.furrowb.scheduler.model.AvailabilityRequest
import com.furrowb.scheduler.model.AvailabilityResponse
import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.service.ReservationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

// The error responses for invalid request data doesn't include WHICH field is invalid. I'd like to improve this by
// catching the validator errors in the @ControllerAdvice but the exceptions are wrapped. I'd have to investigate that further
// on how to properly show which fields are invalid.
@RestController
@RequestMapping("/v1")
class ReservationController(@Autowired private val reservationService: ReservationService) {

    @PostMapping("/reservation")
    fun createReservation(@RequestBody @Validated request: ReservationRequest): ResponseEntity<Reservation> {
        val response = reservationService.scheduleReservation(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/reservation")
    fun checkAvailability(@RequestBody @Validated request: AvailabilityRequest): ResponseEntity<AvailabilityResponse> {
        val response = reservationService.checkAvailability(request)
        if (response.isAvailable) {
            return ResponseEntity.status(HttpStatus.OK).body(response)
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    @DeleteMapping("/reservation")
    fun deleteReservation() {

    }
}