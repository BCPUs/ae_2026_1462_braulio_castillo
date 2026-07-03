package com.pucetec.events.entities

import jakarta.persistence.*

@Entity
@Table(name = "events")
class Event(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val venue: String,
    val totalTickets: Int,
    var availableTickets: Int
)

