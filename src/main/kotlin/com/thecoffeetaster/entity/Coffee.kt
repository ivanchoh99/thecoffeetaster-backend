package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "coffee")
open class Coffee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    var uuid: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "bean_type_id")
    var beanType: BeanType? = null,

    @Column(nullable = false)
    var origin: String = "",

    @Column(nullable = false)
    var altitude: Int = 0,

    @Column(nullable = false)
    var brand: String = "",

    @Column(nullable = false)
    var process: String = "",

    @Column(name = "tasting_notes", nullable = false)
    var tastingNotes: String = "",

    @Column(nullable = false)
    var roaster: String = "",

    @Column(name = "roast_level", nullable = false)
    var roastLevel: String = "",

    @Column(nullable = false)
    var price: String = "",

    var date: LocalDate? = null
)
