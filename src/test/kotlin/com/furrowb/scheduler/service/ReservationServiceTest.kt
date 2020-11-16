package com.furrowb.scheduler.service

import com.furrowb.scheduler.model.AvailabilityRequest
import com.furrowb.scheduler.model.DeletionRequest
import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.repository.ReservationRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

// This is more of an integration test since it's using the H2 database for its calls.
// I'd prefer to handle integration tests in a different directory/structure so it's more obvious but wanted to make sure
// I tested the database logic properly.
// I also wanted to test this with kotest but Spring+Kotest causes issues when trying to use @Autowired so had to revert
// back to just using plan JUnit style.
@ExtendWith(SpringExtension::class)
@SpringBootTest
class ReservationServiceTest {
    @Autowired
    private lateinit var service: ReservationService

    @Autowired
    private lateinit var repository: ReservationRepository

    @BeforeEach
    fun beforeEach() {
        repository.deleteAll()
    }

    @Test
    fun `scheduleReservation returns a Reservation entity upon success`() {
        val request = ReservationRequest(OffsetDateTime.now(), 5, "user1")
        val result = service.scheduleReservation(request)
        result.startDateTime shouldBe request.startDateTime.withOffsetSameInstant(ZoneOffset.UTC)
        result.endDateTime shouldBe request.startDateTime.plusMinutes(request.durationInMinutes.toLong()).withOffsetSameInstant(ZoneOffset.UTC)
        result.user shouldBe request.user
    }

    @Test
    fun `scheduleReservation throws an exception when two reservations have the same time from different timezones`() {
        val dateTime1 = OffsetDateTime.now(ZoneId.of("UTC+01:00"))
        val dateTime2 = dateTime1.withOffsetSameInstant(ZoneOffset.UTC)
        val request1 = ReservationRequest(dateTime1, 5, "user1")
        val request2 = ReservationRequest(dateTime2, 5, "user2")

        service.scheduleReservation(request1)

        shouldThrow<EntityExistsException> {
            service.scheduleReservation(request2)
        }
    }

    @Test
    fun `checkAvailability returns an AvailabilityResponse with isAvailable as true when time slot is available`() {
        val request = AvailabilityRequest(OffsetDateTime.now(), 5)
        val result = service.checkAvailability(request)

        result.isAvailable shouldBe true
        result.startDateTime shouldBe request.startDateTime.withOffsetSameInstant(ZoneOffset.UTC)
        result.endDateTime shouldBe request.startDateTime.plusMinutes(request.durationInMinutes.toLong()).withOffsetSameInstant(ZoneOffset.UTC)
        result.message shouldBe "The specified time is available"
    }

    @Test
    fun `checkAvailability returns an AvailabilityResponse with isAvailable as false when time slot is not available`() {
        val request = AvailabilityRequest(OffsetDateTime.now(), 5)
        val reservation = service.scheduleReservation(ReservationRequest(request.startDateTime, request.durationInMinutes, "user"))
        val result = service.checkAvailability(request)

        reservation.id shouldBeGreaterThan (0L)
        result.isAvailable shouldBe false
        result.startDateTime shouldBe request.startDateTime.withOffsetSameInstant(ZoneOffset.UTC)
        result.endDateTime shouldBe request.startDateTime.plusMinutes(request.durationInMinutes.toLong()).withOffsetSameInstant(ZoneOffset.UTC)
        result.message shouldBe "The specified time is currently not available"
    }

    @Test
    fun `deleteReservation should return the reservation it deleted upon success`() {
        val request = DeletionRequest(OffsetDateTime.now(), 5)
        val reservation = service.scheduleReservation(ReservationRequest(request.startDateTime, request.durationInMinutes, "user"))
        val result = service.deleteReservation(request)

        reservation.id shouldBe result.id
    }

    @Test
    fun `deleteReservation should throw an exception when the reservation could not be found`() {
        val request = DeletionRequest(OffsetDateTime.now(), 5)
        shouldThrow<EntityNotFoundException> {
            service.deleteReservation(request)
        }
    }
}