package com.donmanuelito.minimarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "nota_credito")
public class NotaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotaCredito;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaEmisionNota;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotalNota;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "id_comprobante", nullable = false)
    private Comprobante comprobante;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @PrePersist
    void onCreate() {
        fechaEmisionNota = LocalDate.now();
    }
}
