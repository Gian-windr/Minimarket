package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.EstadoDevolucion;
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
@Table(name = "detalle_devolucion")
public class DetalleDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleDevolucion;

    @Column(nullable = false)
    private Integer cantidadDevolucion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDevolucion estadoDevolucion = EstadoDevolucion.PROCESADA;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_devolucion", nullable = false)
    private Devolucion devolucion;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    /** Linea de venta original a la que corresponde la devolucion. */
    @ManyToOne
    @JoinColumn(name = "id_detalle_venta")
    private DetalleVenta detalleVenta;
}
