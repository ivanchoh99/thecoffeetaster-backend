package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.util.Date
import java.util.UUID

@Entity
open class Coffee(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    var beanType: BeanType? = null,

    var origin: String = "",

    var altitude: Int = 0,

    var brand: String = "",

    var process: String = "",

    var tastingNotes: String = "",

    var roaster: String = "",

    var roastLevel: String = "",

    var price: String = "",

    var date: Date? = null
)
