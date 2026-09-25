package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Caja;
import com.donmanuelito.minimarket.model.enums.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Integer> {

    Optional<Caja> findFirstByEmpleadoIdEmpleadoAndEstadoCaja(Integer idEmpleado, EstadoCaja estado);

    List<Caja> findByEstadoCaja(EstadoCaja estado);
}
