package com.furrowb.scheduler.model

import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ReserveRequest(
        @field:NotNull
        val startDateTime: LocalDateTime,

        @field:NotNull
        val durationInMinutes: Int,

        @field:NotNull
        val timezone: String,

        @field:NotNull
        val user: String // I'd prefer to have this as an ID and have a user table
)