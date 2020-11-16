package com.furrowb.scheduler.controller

import com.furrowb.scheduler.model.entity.Reservation
import com.furrowb.scheduler.testConfig.TestConfig
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

data class ReserveTestScenario(val name: String, val json: String)

@WebMvcTest(AvailabilityController::class)
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
            every { TestConfig.reservationRepoMock.findSchedulingConflicts(any(), any()) } returns emptyList()
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
            ReserveTestScenario("startDateTime", """
                        {
                            "durationInMinutes": 5,
                            "user": "The User"
                        }
                """.trimIndent()),
            ReserveTestScenario("durationInMinutes", """
                        {
                            "startDateTime": "2020-12-03T10:15:30+01:00",
                            "user": "The User"
                        }
                """.trimIndent()),
            ReserveTestScenario("user", """
                        {
                            "startDateTime": "2020-12-03T10:15:30+01:00",
                            "durationInMinutes": 5
                        }
                """.trimIndent()),
        ).forEach {
            it("returns a 400 if ${it.name} is missing") {
                mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(it.json))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest)
            }
        }

        it("returns 409 if a scheduling conflict exists") {
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isCreated)

            // Reset the mock to return a value on the next call
            clearMocks(TestConfig.reservationRepoMock)
            every { TestConfig.reservationRepoMock.findSchedulingConflicts(any(), any()) } returns listOf(Reservation())

            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(MockMvcResultMatchers.status().isConflict)
        }
    }
})