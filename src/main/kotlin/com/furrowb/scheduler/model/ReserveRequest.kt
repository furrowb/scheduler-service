package com.furrowb.scheduler.model

import org.hibernate.annotations.NotFound
import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class ReserveRequest(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        @field:NotNull(message = "startDateTime must be specified")
        val startDateTime: OffsetDateTime,
        @field:NotNull(message = "durationInMinutes must be specified")
        @field:Min(value = 1, message = "durationInMinutes must be greater than 0")
        val durationInMinutes: Int,
        @field:NotBlank(message = "user must be specified")
        val user: String // I'd prefer to have this as an ID and have a user table
)