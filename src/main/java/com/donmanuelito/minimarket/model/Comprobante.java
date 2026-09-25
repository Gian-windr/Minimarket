package com.donmanuelito.minimarket.model;

import com.donmanuelito.minimarket.model.enums.TipoComprobante;
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
@Table(name = "comprobante")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComprobante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, length = 30)
    private String numeroComprobante;

    @Column(nullable = false, length = 10)
    private String serieComprobante;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmisionComprobante;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotalComprobante;

    /** RUC del minimarket (emisor). */
    @Column(nullable = false, length = 11)
    private String rucEmisor;

    /** Razon social del cliente (solo factura). */
    @Column(length = 100)
    private String razonSocial;

    /** Direccion fiscal del cliente (solo factura). */
    @Column(length = 100)
    private String direccionFiscal;

    /** DNI (boleta) o RUC (factura) del cliente. */
    @Column(length = 11)
    private String numDocumento;

    @Column(length = 100)
    private String nombreCliente;

    @Column(columnDefinition = "TEXT")
    private String xmlPath;

    @Column(columnDefinition = "TEXT")
    private String pdfPath;

    @OneToOne
    @JoinColumn(name = "id_venta", nullable = false, unique = true)
    private Venta venta;

    @PrePersist
    void onCreate() {
        fechaEmisionComprobante = LocalDateTime.now();
    }
}
