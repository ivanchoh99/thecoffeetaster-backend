package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "bean_type")
open class BeanType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    var uuid: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var name: String = ""
)
