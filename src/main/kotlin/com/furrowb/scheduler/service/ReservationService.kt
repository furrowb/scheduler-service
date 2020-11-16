package com.furrowb.scheduler.service

import com.furrowb.scheduler.model.AvailabilityRequest
import com.furrowb.scheduler.model.AvailabilityResponse
import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.repository.ReservationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import javax.persistence.EntityExistsException

@Service
class ReservationService(@Autowired private val reservationRepo: ReservationRepository) {
    fun checkAvailability(request: AvailabilityRequest): AvailabilityResponse {
        val endDateTime = calculateEndTime(request.startDateTime, request.durationInMinutes)
        val conflicts = reservationRepo.getSchedulingConflicts(request.startDateTime, calculateEndTime(request.startDateTime, request.durationInMinutes))

        if(conflicts.isNotEmpty()) {
            return AvailabilityResponse(
                    isAvailable = false,
                    message = "The specified time is currently not available",
                    startDateTime = request.startDateTime,
                    endDateTime = endDateTime
            )
        }

        return AvailabilityResponse(
                isAvailable = true,
                message = "The specified time is available",
                startDateTime = request.startDateTime,
                endDateTime = endDateTime
        )
    }

    fun scheduleReservation(request: ReservationRequest): Reservation {
        return persistReservation(request)
    }

    @Transactional
    private fun persistReservation(request: ReservationRequest): Reservation {
        val endDateTime = calculateEndTime(request.startDateTime, request.durationInMinutes)
        val conflicts = reservationRepo.getSchedulingConflicts(request.startDateTime, endDateTime)

        if (conflicts.isNotEmpty()) {
            throw EntityExistsException("Request for start time ${request.startDateTime} conflicts with another reservation")
        }

        val entity = Reservation(startDateTime = request.startDateTime, endDateTime = endDateTime, user = request.user)
        return reservationRepo.save(entity)
    }

    private fun calculateEndTime(startDateTime: OffsetDateTime, durationInMinutes: Int): OffsetDateTime {
        return startDateTime.plusMinutes(durationInMinutes.toLong())
    }
}