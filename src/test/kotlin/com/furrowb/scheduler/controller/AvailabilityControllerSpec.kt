package com.furrowb.scheduler.controller

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders


@WebMvcTest(AvailabilityController::class)
class AvailabilityControllerSpec: DescribeSpec ({
    lateinit var mockMvc: MockMvc

    describe("Reserve endpoint") {
        beforeEach {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(AvailabilityController())
                    .build()
        }

        it("returns a 200") {
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/availability/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "startDateTime": "2020-10-11T06:30:00",
                            "durationInMinutes": 5,
                            "timezone": "Blah",
                            "user": "The User"
                        }
                        """.trimIndent()))
                    .andExpect(MockMvcResultMatchers.status().isOk)
        }
    }
})