package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer stockAnterior;

    @Column(nullable = false)
    private Integer stockNuevo;

    /** Origen del movimiento, ej: "VENTA #12", "COMPRA #3", "AJUSTE". */
    @Column(length = 100)
    private String referencia;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @PrePersist
    void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
}
