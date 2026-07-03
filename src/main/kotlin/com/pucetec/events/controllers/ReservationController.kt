package com.pucetec.events.controllers

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.services.ReservationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservations")
class ReservationController(private val reservationService: ReservationService) {

    @GetMapping
    fun getAll(): List<ReservationResponse> = reservationService.getAllReservations()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ReservationRequest): ReservationResponse = reservationService.createReservation(request)

    @PutMapping("/{id}/cancel")
    fun cancel(@PathVariable id: Long): ReservationResponse = reservationService.cancelReservation(id)
}