package com.donmanuelito.minimarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAuditoria;

    @Column(nullable = false, length = 100)
    private String accionAuditoria;

    @Column(length = 50)
    private String tablaAfectada;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaHoraAuditoria;

    @Column(columnDefinition = "TEXT")
    private String descripcionAuditoria;

    @Column(length = 45)
    private String ipClienteAuditoria;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    void onCreate() {
        fechaHoraAuditoria = LocalDateTime.now();
    }
}
