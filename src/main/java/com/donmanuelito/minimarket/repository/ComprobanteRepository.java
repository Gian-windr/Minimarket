package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Comprobante;
import com.donmanuelito.minimarket.model.enums.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {

    long countByTipoComprobante(TipoComprobante tipo);

    Optional<Comprobante> findByVentaIdVenta(Integer idVenta);

    boolean existsByVentaIdVenta(Integer idVenta);
}
