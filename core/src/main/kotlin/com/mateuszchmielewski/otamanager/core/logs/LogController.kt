package com.mateuszchmielewski.otamanager.core.logs

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller()
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RequestMapping("/log")
class LogController(private val logService: LogService) {
    @PostMapping("/add")
    fun addLog(@RequestBody log: LogEntity): ResponseEntity<LogEntity> {
        val savedLog = logService.saveLog(log)
        return ResponseEntity.ok(savedLog)
    }
}