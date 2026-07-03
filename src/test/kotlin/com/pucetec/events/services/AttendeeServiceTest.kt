package com.pucetec.events.services

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.entities.Attendee
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.repositories.AttendeeRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AttendeeServiceTest {

    @Mock
    lateinit var attendeeRepository: AttendeeRepository

    @InjectMocks
    lateinit var attendeeService: AttendeeService

    @Test
    fun `createAttendee should throw BlankFieldException when name is blank`() {
        val request = AttendeeRequest(name = " ", email = "braulio@mail.com")
        assertThrows<BlankFieldException> {
            attendeeService.createAttendee(request)
        }
    }

    @Test
    fun `createAttendee should throw BlankFieldException when email is blank`() {
        val request = AttendeeRequest(name = "Braulio", email = " ")
        assertThrows<BlankFieldException> {
            attendeeService.createAttendee(request)
        }
    }

    @Test
    fun `createAttendee should save and return AttendeeResponse when request is valid`() {
        val request = AttendeeRequest(name = "Braulio", email = "braulio@mail.com")
        val savedAttendee = Attendee(id = 1L, name = "Braulio", email = "braulio@mail.com")

        `when`(attendeeRepository.save(any(Attendee::class.java))).thenReturn(savedAttendee)

        val response = attendeeService.createAttendee(request)

        assertNotNull(response)
        assertEquals(1L, response.id)
        assertEquals("Braulio", response.name)
    }
}