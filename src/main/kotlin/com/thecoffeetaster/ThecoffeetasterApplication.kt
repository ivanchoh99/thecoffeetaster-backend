package com.thecoffeetaster

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ThecoffeetasterApplication

fun main(args: Array<String>) {
    SpringApplication.run(ThecoffeetasterApplication::class.java, *args)
}
