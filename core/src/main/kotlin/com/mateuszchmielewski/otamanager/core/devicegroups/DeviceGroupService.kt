package com.mateuszchmielewski.otamanager.core.devicegroups

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class DeviceGroupService(private val deviceGroupRepository: DeviceGroupRepository) {
    fun listDeviceGroups(): List<DeviceGroupEntity> {
        return deviceGroupRepository.findAll()
    }

    fun getDeviceGroupById(id: UUID): DeviceGroupEntity? {
        return deviceGroupRepository.findByIdOrNull(id)
    }
}