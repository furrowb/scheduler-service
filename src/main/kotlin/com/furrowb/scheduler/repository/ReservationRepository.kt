package com.furrowb.scheduler.repository

import com.furrowb.scheduler.model.entity.Reservation
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReservationRepository: CrudRepository<Reservation, Long> {

    // Would prefer this to only return a single Reservation since that's all it should ever return.
    // Since I can't use LIMIT, I would have to investigate further on how to limit the results via Hibernate/JPA.
    @Query("SELECT r FROM Reservation r WHERE :start <= r.endDateTime AND r.startDateTime <= :end")
    fun getSchedulingConflicts(@Param("start") start: OffsetDateTime, @Param("end") end: OffsetDateTime): List<Reservation>

    fun getReservationById(id: Long): Reservation

    fun deleteReservationById(id: Long)
}