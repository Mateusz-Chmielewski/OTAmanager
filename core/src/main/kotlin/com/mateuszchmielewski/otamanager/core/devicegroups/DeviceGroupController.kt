package com.mateuszchmielewski.otamanager.core.devicegroups

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller()
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RequestMapping("/devicegroup")
class DeviceGroupController(private val deviceGroupService: DeviceGroupService) {

    @GetMapping("/list")
    fun listDeviceGroups(): ResponseEntity<List<DeviceGroupEntity>> {
        val deviceGroups = deviceGroupService.listDeviceGroups()
        return ResponseEntity.ok(deviceGroups)
    }

    @GetMapping("/{id}")
    fun getDeviceGroupById(@PathVariable("id") id: UUID): ResponseEntity<DeviceGroupEntity> {
        val deviceGroup = deviceGroupService.getDeviceGroupById(id)
        return if (deviceGroup != null) {
            ResponseEntity.ok(deviceGroup)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}