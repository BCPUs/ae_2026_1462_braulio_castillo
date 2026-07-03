package com.pucetec.events.services

import com.pucetec.events.dto.EventRequest
import com.pucetec.events.dto.EventResponse
import com.pucetec.events.entities.Event
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.InvalidCapacityException
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.EventRepository
import org.springframework.stereotype.Service

@Service
class EventService(private val eventRepository: EventRepository) {

    fun createEvent(request: EventRequest): EventResponse {
        if (request.name.isBlank() || request.venue.isBlank()) {
            throw BlankFieldException("Name and venue cannot be blank")
        }
        if (request.totalTickets < 1) {
            throw InvalidCapacityException("Tickets must be at least 1")
        }

        val event = Event(
            name = request.name,
            venue = request.venue,
            totalTickets = request.totalTickets,
            availableTickets = request.totalTickets
        )
        return eventRepository.save(event).toResponse()
    }

    fun getAllEvents(): List<EventResponse> = eventRepository.findAll().map { it.toResponse() }

    fun getEventById(id: Long): EventResponse = eventRepository.findById(id)
        .orElseThrow { EventNotFoundException("Event not found") }
        .toResponse()
}