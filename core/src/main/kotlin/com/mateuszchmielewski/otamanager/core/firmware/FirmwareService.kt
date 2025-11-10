package com.mateuszchmielewski.otamanager.core.firmware

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FirmwareService(
    private val firmwareRepository: FirmwareRepository
) {

    fun getFirmwareHistoryByDeviceId(id: UUID): List<FirmwareEntity> {
        return firmwareRepository.findAll().filter { it.deviceId == id }
    }
}