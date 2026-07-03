package com.pucetec.events.mappers

import com.pucetec.events.dto.AttendeeResponse
import com.pucetec.events.entities.Attendee

fun Attendee.toResponse() = AttendeeResponse(
    id = this.id!!,
    name = this.name,
    email = this.email
)