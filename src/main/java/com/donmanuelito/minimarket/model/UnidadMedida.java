package com.donmanuelito.minimarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "unidad_medida")
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUnidadMedida;

    @Column(nullable = false, unique = true, length = 20)
    private String nombreUnidad;

    public UnidadMedida(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }
}
