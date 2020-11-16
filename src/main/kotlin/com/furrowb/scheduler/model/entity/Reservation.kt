package com.furrowb.scheduler.model.entity

import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reservations")
data class Reservation(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = 0, // default to 0 so auto-generation will provide the appropriate value

        @field:NotNull
        @Column
        val startDateTime: OffsetDateTime,

        @field:NotNull
        @Column
        val endDateTime: OffsetDateTime,

        @field: NotEmpty
        @Column
        val user: String
)