package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.util.Date
import java.util.UUID

@Entity
open class CoffeeReview(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    var coffee: Coffee? = null,

    var author: String = "",

    var date: Date? = null,

    var brewMethod: String = "",

    var rating: Byte = 0
)
