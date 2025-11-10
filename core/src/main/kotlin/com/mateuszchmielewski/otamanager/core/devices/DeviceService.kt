package com.mateuszchmielewski.otamanager.core.devices

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeviceService(private val deviceRepository: DeviceRepository) {
    fun listDevices(): List<DeviceEntity> {
        return deviceRepository.findAll()
    }

    fun getDeviceById(id: UUID): DeviceEntity? {
        return deviceRepository.findByIdOrNull(id)
    }
}