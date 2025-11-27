package com.mateuszchmielewski.otamanager.core.firmware

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FirmwareRepository : JpaRepository<FirmwareEntity, UUID> {
}