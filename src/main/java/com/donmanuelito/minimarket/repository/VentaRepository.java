package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.MetodoPago;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByEstadoVentaAndFechaVentaBetween(EstadoVenta estado, LocalDateTime desde, LocalDateTime hasta);

    List<Venta> findByClienteIdCliente(Integer idCliente);

    List<Venta> findByCajaIdCaja(Integer idCaja);

    @Query("""
            select coalesce(sum(v.totalVenta), 0) from Venta v
            where v.caja.idCaja = :idCaja
              and v.metodoPago = :metodoPago
              and v.estadoVenta = :estado
            """)
    BigDecimal sumTotalByCajaAndMetodoPago(
            @Param("idCaja") Integer idCaja,
            @Param("metodoPago") MetodoPago metodoPago,
            @Param("estado") EstadoVenta estado);

    @Query("""
            select v.cliente.idCliente, v.cliente.nombreCliente, v.cliente.apellidoCliente,
                   count(v), coalesce(sum(v.totalVenta), 0)
            from Venta v
            where v.cliente is not null and v.estadoVenta = :estado
            group by v.cliente.idCliente, v.cliente.nombreCliente, v.cliente.apellidoCliente
            order by count(v) desc
            """)
    List<Object[]> findClientesFrecuentes(@Param("estado") EstadoVenta estado, Pageable pageable);
}
