package com.mateuszchmielewski.otamanager.core.firmware

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/firmware")
class FirmwareController(
    @Value("classpath:blink.bin")
    private var binaryFile: Resource
) {
    @GetMapping("/test")
    fun getTestBinaryFile(): ResponseEntity<Resource> {

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"${binaryFile.filename}\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(binaryFile.contentLength())
            .body(binaryFile)
    }
}
