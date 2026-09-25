package com.donmanuelito.minimarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categoria_productos")
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoriaProductos;

    @Column(nullable = false, unique = true, length = 30)
    private String nombreCategoria;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
