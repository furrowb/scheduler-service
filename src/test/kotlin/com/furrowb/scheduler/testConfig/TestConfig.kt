package com.furrowb.scheduler.testConfig

import com.furrowb.scheduler.controller.ReservationController
import com.furrowb.scheduler.repository.ReservationRepository
import com.furrowb.scheduler.service.ReservationService
import io.mockk.mockk

class TestConfig {
    fun createAvailabilityController(): ReservationController {
        val service = ReservationService(reservationRepoMock)
        return ReservationController(service)
    }

    companion object {
        val reservationRepoMock = mockk<ReservationRepository>(relaxUnitFun = true)
    }
}