package com.mateuszchmielewski.otamanager.core.firmware

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class FirmwareService(
    private val firmwareRepository: FirmwareRepository,
    @Value("\${firmware.storage.path:./firmware-storage}")
    private val firmwareStoragePath: String
) {
    private val storagePath: Path = Paths.get(firmwareStoragePath).toAbsolutePath().normalize()

    init {
        Files.createDirectories(storagePath)
    }

    fun getFirmwareHistoryByDeviceId(id: UUID): List<FirmwareEntity> {
        return firmwareRepository.findAll().filter { it.deviceId == id }
    }

    fun storeFirmwareFile(
        deviceId: UUID,
        fileBytes: ByteArray,
        originalFilename: String,
        version: String,
    ): FirmwareEntity {
        val firmwareEntity = FirmwareEntity(
            deviceId = deviceId,
            version = version,
            description = originalFilename,
        )
        val newFilename = firmwareEntity.id.toString() + ".bin"
        val targetLocation = storagePath.resolve(newFilename)

        Files.write(targetLocation, fileBytes)

        firmwareRepository.save(firmwareEntity)

        return firmwareEntity
    }

}