package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.EstadoBackup;
import com.donmanuelito.minimarket.model.enums.TipoBackup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "backup_log")
public class BackupLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBackup;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaBackup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoBackup tipoBackup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoBackup estadoBackup;

    @Column(length = 255)
    private String ubicacionArchivoBackup;

    @Column(columnDefinition = "TEXT")
    private String descripcionBackup;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable")
    private Usuario usuarioResponsable;

    @PrePersist
    void onCreate() {
        fechaBackup = LocalDateTime.now();
    }
}
