package com.pucetec.events.services

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.entities.Reservation
import com.pucetec.events.entities.ReservationStatus
import com.pucetec.events.exceptions.*
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.AttendeeRepository
import com.pucetec.events.repositories.EventRepository
import com.pucetec.events.repositories.ReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val attendeeRepository: AttendeeRepository,
    private val eventRepository: EventRepository
) {

    @Transactional
    fun createReservation(request: ReservationRequest): ReservationResponse {
        val attendee = attendeeRepository.findById(request.attendeeId)
            .orElseThrow { AttendeeNotFoundException("Attendee not found") }

        val event = eventRepository.findById(request.eventId)
            .orElseThrow { EventNotFoundException("Event not found") }

        if (event.availableTickets <= 0) {
            throw SoldOutException("Event is sold out")
        }

        val activeReservations = reservationRepository.countByAttendeeIdAndStatus(attendee.id!!, ReservationStatus.ACTIVE)
        if (activeReservations >= 4) {
            throw ReservationLimitExceededException("Attendee cannot have more than 4 active reservations")
        }

        event.availableTickets -= 1
        eventRepository.save(event)

        val reservation = Reservation(
            attendee = attendee,
            event = event,
            status = ReservationStatus.ACTIVE,
            createdAt = LocalDateTime.now()
        )

        return reservationRepository.save(reservation).toResponse()
    }

    @Transactional
    fun cancelReservation(id: Long): ReservationResponse {
        val reservation = reservationRepository.findById(id)
            .orElseThrow { ReservationNotFoundException("Reservation not found") }

        if (reservation.status == ReservationStatus.CANCELLED) {
            throw ReservationAlreadyCancelledException("Reservation is already cancelled")
        }

        reservation.status = ReservationStatus.CANCELLED
        reservation.event.availableTickets += 1

        eventRepository.save(reservation.event)
        return reservationRepository.save(reservation).toResponse()
    }

    fun getAllReservations(): List<ReservationResponse> = reservationRepository.findAll().map { it.toResponse() }
}