package com.furrowb.scheduler.controller

import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.testConfig.TestConfig
import io.kotest.assertions.fail
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.clearMocks
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime

data class ReserveTestScenario(val scenario: String, val json: String)

// There's a lot of duplication in these tests. I would like to clean up the mockMvc calls to reduce boilerplate.
// Also discovered that Kotest's DescribeSpec doesn't displayed well in the Gradle test results webpage since it doesn't
// nest the tests or combine the full describe name to make them easy to read like you'd expect in Karma or the like.
@WebMvcTest(ReservationController::class)
class AvailabilityControllerSpec(): DescribeSpec ({
    lateinit var mockMvc: MockMvc
    val endpoint = "/v1/reservation"

    describe("Creating a reservation") {
        val content = """
                {
                    "startDateTime": "2020-12-03T10:15:30+01:00",
                    "durationInMinutes": 5,
                    "user": "The User"
                }
            """.trimIndent()

        beforeEach {
            every { TestConfig.reservationRepoMock.getSchedulingConflicts(any(), any()) } returns emptyList()
            every { TestConfig.reservationRepoMock.save(any()) } returns Reservation(0, OffsetDateTime.now(), OffsetDateTime.now(), "user")
            mockMvc = MockMvcBuilders
                    .standaloneSetup(TestConfig().createAvailabilityController())
                    .setControllerAdvice(RestExceptionHandler())
                    .build()
        }

        it("returns a 201") {
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isCreated)
        }

        listOf(
            ReserveTestScenario("startDateTime is missing", """
                        {
                            "durationInMinutes": 5,
                            "user": "The User"
                        }
                """.trimIndent()),
            ReserveTestScenario("startDateTime is not a valid date", """
                    {
                        "startDateTime": "2020-13-03T10:15:30+01:00",
                        "durationInMinutes": 5,
                        "user": "The User"
                    }
            """.trimIndent()),
            ReserveTestScenario("durationInMinutes is missing", """
                        {
                            "startDateTime": "2020-12-03T10:15:30+01:00",
                            "user": "The User"
                        }
                """.trimIndent()),
            ReserveTestScenario("durationInMinutes is less than 1", """
                    {
                        "startDateTime": "2020-12-03T10:15:30+01:00",
                        "durationInMinutes": 0,
                        "user": "The User"
                    }
            """.trimIndent()),
            ReserveTestScenario("user is missing", """
                        {
                            "startDateTime": "2020-12-03T10:15:30+01:00",
                            "durationInMinutes": 5
                        }
                """.trimIndent()),
            ReserveTestScenario("user is empty", """
                    {
                        "startDateTime": "2020-12-03T10:15:30+01:00",
                        "durationInMinutes": 5,
                        "user": ""
                    }
            """.trimIndent()),
        ).forEach {
            it("returns a 400 if ${it.scenario}") {
                mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(it.json))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest)
            }
        }

        it("returns 409 if a scheduling conflict exists") {
            clearMocks(TestConfig.reservationRepoMock)
            every { TestConfig.reservationRepoMock.getSchedulingConflicts(any(), any()) } returns listOf(Reservation())

            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isConflict)
        }
    }

    describe("Check availability of reservation") {
        val content = """
                {
                    "startDateTime": "2020-12-03T10:15:30+01:00",
                    "durationInMinutes": 5
                }
            """.trimIndent()

        beforeEach {
            every { TestConfig.reservationRepoMock.getSchedulingConflicts(any(), any()) } returns emptyList()
            mockMvc = MockMvcBuilders
                    .standaloneSetup(TestConfig().createAvailabilityController())
                    .setControllerAdvice(RestExceptionHandler())
                    .build()
        }

        it("returns 200 if timeslot is available") {
            mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isOk)
        }

        it("returns 409 if the timeslot is not available") {
            clearMocks(TestConfig.reservationRepoMock)
            every { TestConfig.reservationRepoMock.getSchedulingConflicts(any(), any()) } returns listOf(Reservation())

            mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isConflict)
        }

        it("returns 400 if startDateTime has an invalid date") {
            val badRequest = """
                {
                    "startDateTime": "2020-13-03T10:15:30+01:00",
                    "durationInMinutes": 5
                }
            """.trimIndent()
            mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badRequest))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }

        it("returns 400 if durationInMinutes is less than 1") {
            val badRequest = """
                {
                    "startDateTime": "2020-12-03T10:15:30+01:00",
                    "durationInMinutes": 0
                }
            """.trimIndent()
            mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badRequest))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    describe("Delete reservation") {
        val content = """
                {
                    "startDateTime": "2020-12-03T10:15:30+01:00",
                    "durationInMinutes": 5
                }
            """.trimIndent()

        beforeEach {
            every { TestConfig.reservationRepoMock.getReservationByStartDateTimeAndEndDateTime(any(), any()) } returns
                    listOf(Reservation(1, OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(1), "A user"))
            every { TestConfig.reservationRepoMock.deleteById(any()) } returns Unit
            mockMvc = MockMvcBuilders
                    .standaloneSetup(TestConfig().createAvailabilityController())
                    .setControllerAdvice(RestExceptionHandler())
                    .build()
        }

        it("returns 200 if timeslot was deleted") {
            mockMvc.perform(MockMvcRequestBuilders.delete(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isOk)
        }

        it("returns 400 if startDateTime is invalid") {
            val badRequest = """
                {
                    "startDateTime": "2020-13-03T10:15:30+01:00",
                    "durationInMinutes": 1
                }
            """.trimIndent()
            mockMvc.perform(MockMvcRequestBuilders.delete(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badRequest))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }

        it("returns 400 if durationInMinutes is less than 1") {
            val badRequest = """
                {
                    "startDateTime": "2020-12-03T10:15:30+01:00",
                    "durationInMinutes": 0
                }
            """.trimIndent()
            mockMvc.perform(MockMvcRequestBuilders.delete(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badRequest))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }

        it("returns 404 if timeslot doesn't exist") {
            clearMocks(TestConfig.reservationRepoMock)
            every { TestConfig.reservationRepoMock.getReservationByStartDateTimeAndEndDateTime(any(), any()) } returns emptyList()

            mockMvc.perform(MockMvcRequestBuilders.delete(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }
})