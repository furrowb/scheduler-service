package com.furrowb.scheduler.model

import java.time.OffsetDateTime

data class AvailabilityResponse(
        val isAvailable: Boolean,
        val message: String,
        val startDateTime: OffsetDateTime,
        val endDateTime: OffsetDateTime
)