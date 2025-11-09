package com.mateuszchmielewski.otamanager.core.devices

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller()
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RequestMapping("/device")
class DeviceController {

    @GetMapping("/list")
    fun listDevices(): ResponseEntity<List<DeviceEntity>> {
        val devicesList = listOf(DeviceEntity(UUID.randomUUID(), "kti", "The only ESP32 on KTI"))
        return ResponseEntity.ok(devicesList)
    }
}