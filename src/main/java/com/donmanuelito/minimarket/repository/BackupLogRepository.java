package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.BackupLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupLogRepository extends JpaRepository<BackupLog, Integer> {
}
