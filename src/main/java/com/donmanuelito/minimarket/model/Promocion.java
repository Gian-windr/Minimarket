package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.EstadoPromocion;
import com.donmanuelito.minimarket.model.enums.TipoPromocion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "promocion")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPromocion;

    @Column(nullable = false, length = 50)
    private String nombrePromocion;

    @Column(columnDefinition = "TEXT")
    private String descripcionPromocion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPromocion tipoPromocion;

    /** Porcentaje (0-100) o monto fijo por unidad, segun tipoPromocion. */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPromocion;

    @Column(nullable = false)
    private LocalDate fechaInicioPromo;

    @Column(nullable = false)
    private LocalDate fechaFinPromo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPromocion estadoPromocion = EstadoPromocion.ACTIVA;

    @ManyToMany
    @JoinTable(
            name = "promocion_producto",
            joinColumns = @JoinColumn(name = "id_promocion"),
            inverseJoinColumns = @JoinColumn(name = "id_producto"))
    private List<Producto> productos = new ArrayList<>();
}
