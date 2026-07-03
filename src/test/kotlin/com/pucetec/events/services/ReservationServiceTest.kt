package com.pucetec.events.services

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.entities.*
import com.pucetec.events.exceptions.*
import com.pucetec.events.repositories.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ReservationServiceTest {

    @Mock lateinit var reservationRepository: ReservationRepository
    @Mock lateinit var attendeeRepository: AttendeeRepository
    @Mock lateinit var eventRepository: EventRepository

    @InjectMocks
    lateinit var reservationService: ReservationService

    @Test
    fun `createReservation should throw AttendeeNotFoundException when attendee missing`() {
        val request = ReservationRequest(1L, 1L)
        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<AttendeeNotFoundException> {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation should throw EventNotFoundException when event missing`() {
        val request = ReservationRequest(1L, 1L)
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")

        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<EventNotFoundException> {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation should throw SoldOutException when no tickets available`() {
        val request = ReservationRequest(1L, 1L)
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")
        val event = Event(id = 1L, name = "Charla", venue = "Auditorio", totalTickets = 10, availableTickets = 0)

        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(1L)).thenReturn(Optional.of(event))

        assertThrows<SoldOutException> {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation should throw ReservationLimitExceededException when attendee has 4 active reservations`() {
        val request = ReservationRequest(1L, 1L)
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")
        val event = Event(id = 1L, name = "Charla", venue = "Auditorio", totalTickets = 10, availableTickets = 5)

        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(1L)).thenReturn(Optional.of(event))
        `when`(reservationRepository.countByAttendeeIdAndStatus(1L, ReservationStatus.ACTIVE)).thenReturn(4)

        assertThrows<ReservationLimitExceededException> {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation should decrease available tickets and return ReservationResponse when valid`() {
        val request = ReservationRequest(1L, 1L)
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")
        val event = Event(id = 1L, name = "Charla", venue = "Auditorio", totalTickets = 10, availableTickets = 5)
        val reservation = Reservation(id = 1L, attendee = attendee, event = event, status = ReservationStatus.ACTIVE, createdAt = LocalDateTime.now())

        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(1L)).thenReturn(Optional.of(event))
        `when`(reservationRepository.countByAttendeeIdAndStatus(1L, ReservationStatus.ACTIVE)).thenReturn(2)
        `when`(reservationRepository.save(any(Reservation::class.java))).thenReturn(reservation)

        val response = reservationService.createReservation(request)

        assertNotNull(response)
        assertEquals(4, event.availableTickets) // Decrementó de 5 a 4
        assertEquals("ACTIVE", response.status)
    }

    @Test
    fun `cancelReservation should throw ReservationNotFoundException when reservation missing`() {
        `when`(reservationRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<ReservationNotFoundException> {
            reservationService.cancelReservation(1L)
        }
    }

    @Test
    fun `cancelReservation should throw ReservationAlreadyCancelledException when status is CANCELLED`() {
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")
        val event = Event(id = 1L, name = "Charla", venue = "Auditorio", totalTickets = 10, availableTickets = 5)
        val reservation = Reservation(id = 1L, attendee = attendee, event = event, status = ReservationStatus.CANCELLED, createdAt = LocalDateTime.now())

        `when`(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation))

        assertThrows<ReservationAlreadyCancelledException> {
            reservationService.cancelReservation(1L)
        }
    }

    @Test
    fun `cancelReservation should increase available tickets and change status to CANCELLED`() {
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")
        val event = Event(id = 1L, name = "Charla", venue = "Auditorio", totalTickets = 10, availableTickets = 5)
        val reservation = Reservation(id = 1L, attendee = attendee, event = event, status = ReservationStatus.ACTIVE, createdAt = LocalDateTime.now())

        `when`(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation))
        `when`(reservationRepository.save(any(Reservation::class.java))).thenReturn(reservation)

        val response = reservationService.cancelReservation(1L)

        assertEquals("CANCELLED", response.status)
        assertEquals(6, event.availableTickets) // Incrementó de 5 a 6
    }

    @Test
    fun `getAllReservations should return list of reservations`() {
        val attendee = Attendee(id = 1L, name = "Braulio", email = "b@mail.com")
        val event = Event(id = 1L, name = "Charla", venue = "Auditorio", totalTickets = 10, availableTickets = 5)
        val reservation = Reservation(id = 1L, attendee = attendee, event = event, status = ReservationStatus.ACTIVE, createdAt = LocalDateTime.now())

        `when`(reservationRepository.findAll()).thenReturn(listOf(reservation))

        val result = reservationService.getAllReservations()

        assertEquals(1, result.size)
    }
}