package com.furrowb.scheduler.model

import java.time.LocalDateTime
import java.time.OffsetDateTime

data class ReserveRequest(
        val startDateTime: OffsetDateTime,

        val durationInMinutes: Int,

        val timezone: String,

        val user: String // I'd prefer to have this as an ID and have a user table
)