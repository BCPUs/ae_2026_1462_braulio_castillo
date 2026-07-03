package com.pucetec.events.entities

import jakarta.persistence.*
import java.time.LocalDateTime

enum class ReservationStatus { ACTIVE, CANCELLED }

@Entity
@Table(name = "reservations")
class Reservation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "attendee_id")
    val attendee: Attendee,

    @ManyToOne
    @JoinColumn(name = "event_id")
    val event: Event,

    @Enumerated(EnumType.STRING)
    var status: ReservationStatus,

    var createdAt: LocalDateTime
)