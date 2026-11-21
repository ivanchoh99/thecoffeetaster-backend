package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "coffee_review")
open class CoffeeReview(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    var uuid: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "coffee_id")
    var coffee: Coffee? = null,

    @Column(nullable = false)
    var author: String = "",

    var date: LocalDate? = null,

    @Column(name = "brew_method", nullable = false)
    var brewMethod: String = "",

    @Column(nullable = false)
    var rating: Byte = 0
)
