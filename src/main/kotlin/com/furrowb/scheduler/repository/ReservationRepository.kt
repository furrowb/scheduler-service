package com.furrowb.scheduler.repository

import com.furrowb.scheduler.model.entity.Reservation
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReservationRepository: CrudRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE :start <= r.endDateTime AND r.startDateTime <= :end")
    fun findSchedulingConflicts(@Param("start") start: OffsetDateTime, @Param("end") end: OffsetDateTime): List<Reservation>
}