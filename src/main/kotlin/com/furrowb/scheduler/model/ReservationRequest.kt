package com.furrowb.scheduler.model

import java.time.OffsetDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ReservationRequest(
        // I would like to have a custom validator that checks if the time is in the future and not the past
        @field:NotNull(message = "startDateTime must be specified")
        val startDateTime: OffsetDateTime,
        @field:NotNull(message = "durationInMinutes must be specified")
        @field:Min(value = 1, message = "durationInMinutes must be greater than 0")
        val durationInMinutes: Int,
        @field:NotBlank(message = "user must be specified")
        val user: String // I'd prefer to have this as an ID and have a user table
)