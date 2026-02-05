package com.example.smartthings.repository

import com.example.smartthings.domain.InstalledApp
import org.springframework.data.jpa.repository.JpaRepository

interface InstalledAppRepository : JpaRepository<InstalledApp, String> {

    fun findFirstByOrderByCreatedAtAsc(): InstalledApp?
}
