package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
}
