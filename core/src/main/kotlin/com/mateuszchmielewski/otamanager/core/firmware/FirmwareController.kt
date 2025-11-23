package com.mateuszchmielewski.otamanager.core.firmware

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.core.io.Resource
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/firmware")
class FirmwareController(
    @Value("classpath:blink.bin")
    private var binaryFile: Resource,
    private val firmwareService: FirmwareService
) {
    @GetMapping("/test")
    fun getTestBinaryFile(): ResponseEntity<Resource> {

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"${binaryFile.filename}\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(binaryFile.contentLength())
            .body(binaryFile)
    }

    @GetMapping("/history/{id}")
    fun getFirmwareHistoryByDeviceId(
        @PathVariable("id") id: UUID
    ): ResponseEntity<List<FirmwareEntity>> {
        val firmwareHistory = firmwareService.getFirmwareHistoryByDeviceId(id)
        return if (firmwareHistory.isNotEmpty()) {
            ResponseEntity.ok(firmwareHistory)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/upload/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFirmwareForDevice(
        @PathVariable("id") id: UUID,
        @RequestParam("file") file: MultipartFile,
        @RequestParam("version") version: String,
    ): ResponseEntity<String> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body("File is empty")
        }

        firmwareService.storeFirmwareFile(
            id,
            file.bytes,
            file.originalFilename ?: "unknown",
            version
        )

        return ResponseEntity.ok("File uploaded successfully for device $id")
    }

    @GetMapping("/download/{deviceId}")
    fun downloadFirmwareFile(
        @PathVariable("deviceId") deviceId: UUID,
        @RequestParam("current_version") firmwareId: UUID?,
    ): ResponseEntity<Resource> {
        val firmwareResource = firmwareService.loadFirmwareFileAsResource(deviceId, firmwareId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"${firmwareResource.filename}\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(firmwareResource.contentLength())
            .body(firmwareResource)
    }

    @GetMapping("/version/{deviceId}")
    fun getNewestFirmwareVersionForDevice(
        @PathVariable("deviceId") deviceId: UUID,
    ): ResponseEntity<String> {
        val newestFirmware = firmwareService.getNewestFirmwareForDevice(deviceId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(newestFirmware.id.toString())
    }
}
