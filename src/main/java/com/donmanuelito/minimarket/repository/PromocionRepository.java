package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Promocion;
import com.donmanuelito.minimarket.model.enums.EstadoPromocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PromocionRepository extends JpaRepository<Promocion, Integer> {

    @Query("""
            select p from Promocion p join p.productos pr
            where pr.idProducto = :idProducto
              and p.estadoPromocion = :estado
              and :fecha between p.fechaInicioPromo and p.fechaFinPromo
            """)
    List<Promocion> findPromocionesVigentesPorProducto(
            @Param("idProducto") Integer idProducto,
            @Param("estado") EstadoPromocion estado,
            @Param("fecha") LocalDate fecha);
}
