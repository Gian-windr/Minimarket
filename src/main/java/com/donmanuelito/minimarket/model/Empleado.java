package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.Estado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleado;

    @Column(nullable = false, length = 100)
    private String nombreEmpleado;

    @Column(nullable = false, length = 100)
    private String apellidoEmpleado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estadoEmpleado = Estado.ACTIVO;

    private LocalDateTime fechaContratacion;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}
