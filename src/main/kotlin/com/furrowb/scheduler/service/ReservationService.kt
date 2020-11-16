package com.furrowb.scheduler.service

import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.repository.ReservationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityExistsException

@Service
class ReservationService(@Autowired private val reservationRepo: ReservationRepository) {
    fun scheduleReservation(request: ReservationRequest): Reservation {
        return persistReservation(request)
    }

    @Transactional
    private fun persistReservation(request: ReservationRequest): Reservation {
        val endDateTime = request.startDateTime.plusMinutes(request.durationInMinutes.toLong())
        val conflicts = reservationRepo.findSchedulingConflicts(request.startDateTime, endDateTime)

        if (conflicts.isNotEmpty()) {
            throw EntityExistsException("Request for start time ${request.startDateTime} conflicts with another reservation")
        }

        val entity = Reservation(startDateTime = request.startDateTime, endDateTime = endDateTime, user = request.user)
        return reservationRepo.save(entity)
    }
}