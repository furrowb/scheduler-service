package com.furrowb.scheduler.testConfig

import com.furrowb.scheduler.controller.AvailabilityController
import com.furrowb.scheduler.repository.ReservationRepository
import com.furrowb.scheduler.service.ReservationService
import io.mockk.mockk

class TestConfig {
    fun createAvailabilityController(): AvailabilityController {
        val service = ReservationService(reservationRepoMock)
        return AvailabilityController(service)
    }

    companion object {
        val reservationRepoMock = mockk<ReservationRepository>()
    }
}