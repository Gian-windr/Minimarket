package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.EstadoCompra;
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
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCompra;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCompra;

    @Column(length = 30)
    private String numeroFacturaProveedor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCompra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompra estadoCompra = EstadoCompra.REGISTRADA;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @JsonManagedReference
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    @PrePersist
    void onCreate() {
        fechaCompra = LocalDateTime.now();
    }
}
