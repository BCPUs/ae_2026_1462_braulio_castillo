package com.pucetec.events.services

import com.pucetec.events.dto.EventRequest
import com.pucetec.events.entities.Event
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.InvalidCapacityException
import com.pucetec.events.repositories.EventRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class EventServiceTest {

    @Mock
    lateinit var eventRepository: EventRepository

    @InjectMocks
    lateinit var eventService: EventService

    @Test
    fun `createEvent should throw BlankFieldException when name is blank`() {
        val request = EventRequest(name = " ", venue = "Estadio", totalTickets = 100)
        assertThrows<BlankFieldException> {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent should throw BlankFieldException when venue is blank`() {
        val request = EventRequest(name = "Concierto", venue = " ", totalTickets = 100)
        assertThrows<BlankFieldException> {
            eventService.createEvent(request)
        }
    }
    @Test
    fun `createEvent should throw InvalidCapacityException when totalTickets is less than 1`() {
        val request = EventRequest(name = "Concierto", venue = "Estadio", totalTickets = 0)

        assertThrows<InvalidCapacityException> {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent should save and return EventResponse when request is valid`() {
        val request = EventRequest(name = "Concierto", venue = "Estadio", totalTickets = 100)
        val savedEvent = Event(id = 1L, name = "Concierto", venue = "Estadio", totalTickets = 100, availableTickets = 100)

        `when`(eventRepository.save(any(Event::class.java))).thenReturn(savedEvent)

        val response = eventService.createEvent(request)

        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals(100, response.availableTickets)
    }

    @Test
    fun `getAllEvents should return list of events`() {
        val event = Event(id = 1L, name = "Concierto", venue = "Estadio", totalTickets = 100, availableTickets = 100)
        `when`(eventRepository.findAll()).thenReturn(listOf(event))

        val result = eventService.getAllEvents()

        assertEquals(1, result.size)
    }

    @Test
    fun `getEventById should throw EventNotFoundException when event does not exist`() {
        `when`(eventRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<EventNotFoundException> {
            eventService.getEventById(1L)
        }
    }

    @Test
    fun `getEventById should return EventResponse when event exists`() {
        val event = Event(id = 1L, name = "Concierto", venue = "Estadio", totalTickets = 100, availableTickets = 100)
        `when`(eventRepository.findById(1L)).thenReturn(Optional.of(event))

        val response = eventService.getEventById(1L)

        assertEquals(1L, response.id)
    }
}