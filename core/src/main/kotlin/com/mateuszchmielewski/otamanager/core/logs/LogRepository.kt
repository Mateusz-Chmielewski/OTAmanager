package com.mateuszchmielewski.otamanager.core.logs

import com.mateuszchmielewski.otamanager.core.devices.DeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LogRepository : JpaRepository<LogEntity, UUID> {

}