package com.furrowb.scheduler.controller

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

data class ReserveTestScenario(val name: String, val json: String)

@WebMvcTest(AvailabilityController::class)
class AvailabilityControllerSpec: DescribeSpec ({
    lateinit var mockMvc: MockMvc

    describe("Reserve endpoint") {
        val endpoint = "/v1/availability/reserve"

        beforeEach {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(AvailabilityController())
                    .build()
        }

        it("returns a 200") {
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "startDateTime": "2020-12-03T10:15:30+01:00",
                            "durationInMinutes": 5,
                            "user": "The User"
                        }
                        """.trimIndent()))
                    .andExpect(MockMvcResultMatchers.status().isOk)
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
    }
})