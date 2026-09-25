package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.Estado;
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
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    @Column(nullable = false, length = 100)
    private String nombreProducto;

    @Column(columnDefinition = "TEXT")
    private String descripcionProducto;

    /** Precio de venta al publico (IGV incluido). */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioProducto;

    /** Ultimo precio de compra, para calculo de margen. */
    @Column(precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(nullable = false)
    private Integer stockProducto = 0;

    @Column(nullable = false)
    private Integer stockMinimo = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estadoProducto = Estado.ACTIVO;

    /** Producto vendible fraccionado (por peso/corte). */
    private Boolean tipoCorte = false;

    @Column(columnDefinition = "TEXT")
    private String imagen;

    @Column(unique = true, length = 50)
    private String codigoBarras;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "id_tipo_producto")
    private TipoProducto tipoProducto;

    @ManyToOne
    @JoinColumn(name = "id_categoria_producto")
    private CategoriaProducto categoriaProducto;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida")
    private UnidadMedida unidadMedida;

    @PrePersist
    void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = fechaCreacion;
    }

    @PreUpdate
    void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
