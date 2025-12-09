package com.mateuszchmielewski.otamanager.core.firmware

import com.mateuszchmielewski.otamanager.core.devices.DeviceService
import com.mateuszchmielewski.otamanager.core.mqtt.MqttConfiguration
import com.mateuszchmielewski.otamanager.core.mqtt.MqttPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional
import java.util.UUID

@Service
class FirmwareService(
    private val firmwareRepository: FirmwareRepository,
    @Value("\${firmware.storage.path:./firmware-storage}")
    private val firmwareStoragePath: String,
    private val deviceService: DeviceService,
    private val mqttPublisher: MqttPublisher
) {
    private val storagePath: Path = Paths.get(firmwareStoragePath).toAbsolutePath().normalize()

    init {
        Files.createDirectories(storagePath)
    }

    fun getFirmwareHistoryByDeviceId(id: UUID): List<FirmwareEntity> {
        return firmwareRepository.findAll().filter { it.groupId == id }
    }

    fun storeFirmwareFile(
        groupId: UUID,
        fileBytes: ByteArray,
        originalFilename: String,
        version: String,
    ): FirmwareEntity {
//        val groupId = getDeviceGroupId(deviceId)
        val firmwareEntity = FirmwareEntity(
            groupId = groupId,
            version = version,
            description = originalFilename,
            isActive = true,
        )
        val newFilename = firmwareEntity.id.toString() + ".bin"
        val targetLocation = storagePath.resolve(newFilename)

        Files.write(targetLocation, fileBytes)

        firmwareRepository.save(firmwareEntity)

        notifyGroupAboutNewFirmware(groupId)

        return firmwareEntity
    }

    fun loadFirmwareFileAsResource(deviceId: UUID, firmwareId: UUID?): Resource? {
        val installedFirmware = if (firmwareId != null) firmwareRepository.findById(firmwareId) else Optional.empty()
        val groupId = getDeviceGroupId(deviceId)
        val newestFirmware = firmwareRepository.findAll()
            .filter { it.groupId == groupId && it.isActive }
            .maxByOrNull { it.uploadedAt }

        if (newestFirmware == null) {
            return null
        }

        if (installedFirmware.isEmpty || installedFirmware.get().id != newestFirmware.id) {
            val filename = newestFirmware.id.toString() + ".bin"
            val filePath = storagePath.resolve(filename).normalize()
            return UrlResource(filePath.toUri())
        } else {
            return null
        }
    }

    fun getNewestFirmwareForDevice(deviceId: UUID): FirmwareEntity? {
        val groupId = getDeviceGroupId(deviceId)
        return firmwareRepository.findAll()
            .filter { it.groupId == groupId && it.isActive }
            .maxByOrNull { it.uploadedAt }
    }

    private fun getDeviceGroupId(deviceId: UUID): UUID {
        val device = deviceService.getDeviceById(deviceId) ?: throw IllegalStateException("Device not found")
        return device.groupId
    }

    private fun notifyGroupAboutNewFirmware(groupId: UUID) {
        val devicesInGroup = deviceService.listDevices().filter { it.groupId == groupId }
        for (device in devicesInGroup) {
            notifyDeviceAboutNewFirmware(device.id)
        }
    }

    private fun notifyDeviceAboutNewFirmware(deviceId: UUID) {
        val topic = "ota/$deviceId"
        mqttPublisher.publish(topic, "")
    }
}