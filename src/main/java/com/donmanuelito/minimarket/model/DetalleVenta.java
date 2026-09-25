package com.donmanuelito.minimarket.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleVenta;

    @Column(nullable = false)
    private Integer cantidadDetalle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioDetalle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalDetalle;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
}
