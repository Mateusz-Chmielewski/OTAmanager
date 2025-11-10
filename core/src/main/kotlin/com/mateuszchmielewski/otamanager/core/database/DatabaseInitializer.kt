package com.mateuszchmielewski.otamanager.core.database

import com.mateuszchmielewski.otamanager.core.devices.DeviceEntity
import com.mateuszchmielewski.otamanager.core.devices.DeviceRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

@Configuration
class DatabaseInitializer {
    @Bean
    fun initializeDevices(deviceRepository: DeviceRepository): CommandLineRunner {
        return CommandLineRunner {
            if (deviceRepository.count() == 0L) {
                val device1 = DeviceEntity(
                    name = "ESP32-Office",
                    description = "ESP32 device in office room"
                )

                val device2 = DeviceEntity(
                    name = "ESP32-Lab",
                    description = "ESP32 device in laboratory"
                )

                val device3 = DeviceEntity(
                    name = "ESP32-Workshop",
                    description = "ESP32 device in workshop area"
                )

                val device4 = DeviceEntity(
                    name = "ESP32-Storage",
                    description = "ESP32 device in storage room"
                )

                deviceRepository.saveAll(listOf(device1, device2, device3, device4))
            }
        }
    }
}