package com.mateuszchmielewski.otamanager.core.database

import com.mateuszchmielewski.otamanager.core.devicegroups.DeviceGroupEntity
import com.mateuszchmielewski.otamanager.core.devicegroups.DeviceGroupRepository
import com.mateuszchmielewski.otamanager.core.devices.DeviceEntity
import com.mateuszchmielewski.otamanager.core.devices.DeviceRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

@Configuration
class DatabaseInitializer {
    @Bean
    fun initializeDevices(
        deviceRepository: DeviceRepository,
        deviceGroupRepository: DeviceGroupRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            if (deviceRepository.count() == 0L) {
                val deviceGroup = DeviceGroupEntity(
                    id = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851"),
                    name = "Default Group",
                    description = "This is the default device group",
                    userId = -1L
                )

                val deviceGroup2 = DeviceGroupEntity(
                    id = UUID.fromString("a123f1ee-6c54-4b01-90e6-d701748f0abc"),
                    name = "Secondary Group",
                    description = "This is the secondary device group",
                    userId = -1L
                )

                deviceGroupRepository.save(deviceGroup)
                deviceGroupRepository.save(deviceGroup2)

                val device1 = DeviceEntity(
                    id = UUID.fromString("fe6469ac-8a21-4be4-b5fb-726242d4f918"),
                    name = "ESP32-Office",
                    description = "ESP32 device in office room",
                    groupId = deviceGroup.id
                )

                val device2 = DeviceEntity(
                    name = "ESP32-Lab",
                    description = "ESP32 device in laboratory",
                    groupId = deviceGroup.id
                )

                val device3 = DeviceEntity(
                    name = "ESP32-Workshop",
                    description = "ESP32 device in workshop area",
                    groupId = deviceGroup.id
                )

                val device4 = DeviceEntity(
                    name = "ESP32-Storage",
                    description = "ESP32 device in storage room",
                    groupId = deviceGroup.id
                )

                deviceRepository.saveAll(listOf(device1, device2, device3, device4))
            }
        }
    }
}