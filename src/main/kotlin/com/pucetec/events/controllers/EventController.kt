package com.pucetec.events.controllers

import com.pucetec.events.dto.EventRequest
import com.pucetec.events.dto.EventResponse
import com.pucetec.events.services.EventService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/events")
class EventController(private val eventService: EventService) {

    @GetMapping
    fun getAll(): List<EventResponse> = eventService.getAllEvents()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): EventResponse = eventService.getEventById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: EventRequest): EventResponse = eventService.createEvent(request)
}