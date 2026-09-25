package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.EstadoCaja;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "caja")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCaja;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaApertura;

    private LocalDateTime fechaCierre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicial;

    /** Efectivo esperado al cierre: monto inicial + ventas en efectivo. */
    @Column(precision = 10, scale = 2)
    private BigDecimal montoSistema;

    /** Efectivo contado fisicamente al cierre (arqueo). */
    @Column(precision = 10, scale = 2)
    private BigDecimal montoReal;

    @Column(precision = 10, scale = 2)
    private BigDecimal diferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCaja estadoCaja = EstadoCaja.ABIERTA;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @PrePersist
    void onCreate() {
        fechaApertura = LocalDateTime.now();
    }
}
