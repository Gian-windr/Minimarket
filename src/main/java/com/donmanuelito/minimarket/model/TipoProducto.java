package com.donmanuelito.minimarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tipo_productos")
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoProducto;

    @Column(nullable = false, unique = true, length = 30)
    private String nombreTipoProducto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
