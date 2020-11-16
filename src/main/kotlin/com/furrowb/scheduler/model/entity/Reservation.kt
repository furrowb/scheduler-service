package com.furrowb.scheduler.model.entity

import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reservations")
// Due to JPA needing to extend the class, we must use open.
// Also using `var` and default values for the variables because JPA requires
// a default constructor and getter/setters to update the values
open class Reservation(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @get: Column(name = "id")
        open var id: Long = 0, // default to 0 so auto-generation will provide the appropriate value

        @field:NotNull
        @get: Column(name = "startDateTime")
        open var startDateTime: OffsetDateTime? = null,

        @field:NotNull
        @get: Column(name ="endDateTime")
        open var endDateTime: OffsetDateTime? = null,

        @field: NotEmpty
        @get: Column(name = "user")
        // Like mentioned elsewhere, I would create a one-to-many relationship to user table that would reference an ID
        open var user: String? = null
)