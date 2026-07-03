package com.pucetec.events.services

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.dto.AttendeeResponse
import com.pucetec.events.entities.Attendee
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.AttendeeRepository
import org.springframework.stereotype.Service

@Service
class AttendeeService(private val attendeeRepository: AttendeeRepository) {

    fun createAttendee(request: AttendeeRequest): AttendeeResponse {
        if (request.name.isBlank() || request.email.isBlank()) {
            throw BlankFieldException("Name and email cannot be blank")
        }

        val attendee = Attendee(name = request.name, email = request.email)
        return attendeeRepository.save(attendee).toResponse()
    }
}