package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findAllByOrderByFechaHoraAuditoriaDesc();
}
