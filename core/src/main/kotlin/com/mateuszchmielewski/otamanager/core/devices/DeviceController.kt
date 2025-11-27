package com.mateuszchmielewski.otamanager.core.devices

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller()
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RequestMapping("/device")
class DeviceController(
    private val deviceService: DeviceService
) {

    @GetMapping("/list")
    fun listDevices(): ResponseEntity<List<DeviceEntity>> {
        val devicesList = deviceService.listDevices()
        return ResponseEntity.ok(devicesList)
    }

    @GetMapping("/{id}")
    fun getDeviceById(@PathVariable("id") id: UUID): ResponseEntity<DeviceEntity> {
        val device = deviceService.getDeviceById(id)
        return if (device != null) {
            ResponseEntity.ok(device)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}