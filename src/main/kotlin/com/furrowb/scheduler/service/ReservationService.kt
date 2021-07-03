package com.furrowb.scheduler.service

import com.furrowb.scheduler.model.AvailabilityRequest
import com.furrowb.scheduler.model.AvailabilityResponse
import com.furrowb.scheduler.model.DeletionRequest
import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.repository.ReservationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

@Service
class ReservationService(@Autowired private val reservationRepo: ReservationRepository) {
    fun checkAvailability(request: AvailabilityRequest): AvailabilityResponse {
        val utcRequest = request.copy(startDateTime = convertToUtcTime(request.startDateTime))
        val endDateTime = calculateEndTime(utcRequest.startDateTime, utcRequest.durationInMinutes)
        val conflicts = reservationRepo.getSchedulingConflicts(utcRequest.startDateTime, calculateEndTime(utcRequest.startDateTime, utcRequest.durationInMinutes))

        if(conflicts.isNotEmpty()) {
            return AvailabilityResponse(
                    isAvailable = false,
                    message = "The specified time is currently not available",
                    startDateTime = utcRequest.startDateTime,
                    endDateTime = endDateTime
            )
        }

        return AvailabilityResponse(
                isAvailable = true,
                message = "The specified time is available",
                startDateTime = utcRequest.startDateTime,
                endDateTime = endDateTime
        )
    }

    fun deleteReservation(request: DeletionRequest): Reservation {
        val utcRequest = request.copy(startDateTime = convertToUtcTime(request.startDateTime))
        return persistReservationDeletion(utcRequest)
    }

    @Transactional
    fun persistReservationDeletion(request: DeletionRequest): Reservation {
        val reservation = reservationRepo.getReservationByStartDateTimeAndEndDateTime(request.startDateTime, calculateEndTime(request.startDateTime, request.durationInMinutes))
                ?: throw EntityNotFoundException("A reservation could not be found for a request with the start time of ${request.startDateTime} and duration of ${request.durationInMinutes} minutes")
        reservationRepo.deleteById(reservation.id)
        return reservation
    }

    fun scheduleReservation(request: ReservationRequest): Reservation {
        val utcRequest = request.copy(startDateTime = convertToUtcTime(request.startDateTime))
        return persistReservation(utcRequest)
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

    fun getReservationByID(id: Long): Reservation {
        return reservationRepo.getReservationById(id) ?: throw EntityNotFoundException("No reservation for ID $id")
    }

    private fun calculateEndTime(startDateTime: OffsetDateTime, durationInMinutes: Int): OffsetDateTime {
        return startDateTime.plusMinutes(durationInMinutes.toLong())
    }

    private fun convertToUtcTime(dateTime: OffsetDateTime): OffsetDateTime {
        return dateTime.withOffsetSameInstant(ZoneOffset.UTC)
    }
}