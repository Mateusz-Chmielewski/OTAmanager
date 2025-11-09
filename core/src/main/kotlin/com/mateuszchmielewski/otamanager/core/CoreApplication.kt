package com.mateuszchmielewski.otamanager.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}
