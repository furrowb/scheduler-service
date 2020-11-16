package com.furrowb.scheduler.service

import com.furrowb.scheduler.model.ReservationRequest
import com.furrowb.scheduler.repository.ReservationRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.persistence.EntityExistsException

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

    fun `returns a Reservation entity upon success`() {
        val request = ReservationRequest(OffsetDateTime.now(), 5, "user1")
        val result = service.scheduleReservation(request)
        result.startDateTime shouldBe request.startDateTime
        result.endDateTime shouldBe request.startDateTime.plusMinutes(request.durationInMinutes.toLong())
        result.user shouldBe request.user
    }

    fun `finds a conflict when two reservations have the same time from different timezones`() {
        val dateTime1 = OffsetDateTime.now(ZoneId.of("UTC+01:00"))
        val dateTime2 = dateTime1.atZoneSimilarLocal(ZoneId.of("UTC-02:00")).toOffsetDateTime()
        val request1 = ReservationRequest(dateTime1, 5, "user1")
        val request2 = ReservationRequest(dateTime2, 5, "user1")

        service.scheduleReservation(request1)

        shouldThrow<EntityExistsException> {
            service.scheduleReservation(request2)
        }
    }
}