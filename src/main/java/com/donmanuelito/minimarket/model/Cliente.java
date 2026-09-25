package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.model.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCliente;

    @Column(nullable = false, length = 100)
    private String nombreCliente;

    @Column(length = 100)
    private String apellidoCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, unique = true, length = 15)
    private String numDocumento;

    @Column(length = 15)
    private String telefono;

    @Column(length = 150)
    private String direccion;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estadoCliente = Estado.ACTIVO;

    @Column(updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
