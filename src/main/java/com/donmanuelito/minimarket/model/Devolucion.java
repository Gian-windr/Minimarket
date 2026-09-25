package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.TipoDevolucion;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "devolucion")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDevolucion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaDevolucion;

    @Column(columnDefinition = "TEXT")
    private String motivoDevolucion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDevolucion tipoDevolucion;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @JsonManagedReference
    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucion> detalles = new ArrayList<>();

    @PrePersist
    void onCreate() {
        fechaDevolucion = LocalDateTime.now();
    }
}
