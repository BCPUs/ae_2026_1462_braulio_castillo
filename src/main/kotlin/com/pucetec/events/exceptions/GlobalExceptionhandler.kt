package com.pucetec.events.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ExceptionResponse(
    val message: String?,
    val source: String = "events-microservice"
)

@RestControllerAdvice
class GlobalExceptionHandler {

    // HTTP 400 - Bad Request
    @ExceptionHandler(BlankFieldException::class, InvalidCapacityException::class)
    fun handleBadRequest(e: RuntimeException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(ExceptionResponse(e.message), HttpStatus.BAD_REQUEST)
    }

    // HTTP 404 - Not Found
    @ExceptionHandler(
        AttendeeNotFoundException::class,
        EventNotFoundException::class,
        ReservationNotFoundException::class
    )
    fun handleNotFound(e: RuntimeException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(ExceptionResponse(e.message), HttpStatus.NOT_FOUND)
    }

    // HTTP 409 - Conflict
    @ExceptionHandler(
        SoldOutException::class,
        ReservationLimitExceededException::class,
        ReservationAlreadyCancelledException::class
    )
    fun handleConflict(e: RuntimeException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(ExceptionResponse(e.message), HttpStatus.CONFLICT)
    }
}