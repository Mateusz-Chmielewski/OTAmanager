package com.mateuszchmielewski.otamanager.core.logs

import org.springframework.stereotype.Service

@Service
class LogService(private val logRepository: LogRepository) {
    fun saveLog(log: LogEntity): LogEntity {
        return logRepository.save(log)
    }
}