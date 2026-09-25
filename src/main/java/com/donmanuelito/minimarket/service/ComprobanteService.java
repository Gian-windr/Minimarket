package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.ComprobanteRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Comprobante;
import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.TipoComprobante;
import com.donmanuelito.minimarket.repository.ComprobanteRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final VentaRepository ventaRepository;
    private final ComprobantePdfService comprobantePdfService;

    @Value("${app.empresa.ruc}")
    private String rucEmisor;

    public List<Comprobante> listar() {
        return comprobanteRepository.findAll();
    }

    public Comprobante obtener(Integer id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante", id));
    }

    public Comprobante obtenerPorVenta(Integer idVenta) {
        return comprobanteRepository.findByVentaIdVenta(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La venta #" + idVenta + " no tiene comprobante"));
    }

    /** Emite boleta o factura con serie y correlativo automaticos. */
    @Transactional
    public Comprobante emitir(ComprobanteRequest request) {
        Venta venta = ventaRepository.findById(request.idVenta())
                .orElseThrow(() -> new ResourceNotFoundException("Venta", request.idVenta()));

        if (venta.getEstadoVenta() != EstadoVenta.COMPLETADA) {
            throw new BusinessException("Solo se emite comprobante de ventas completadas");
        }
        if (comprobanteRepository.existsByVentaIdVenta(venta.getIdVenta())) {
            throw new BusinessException("La venta #" + venta.getIdVenta() + " ya tiene comprobante");
        }
        if (request.tipoComprobante() == TipoComprobante.FACTURA
                && (request.numDocumento() == null || request.razonSocial() == null)) {
            throw new BusinessException("La factura requiere RUC y razon social del cliente");
        }

        Comprobante comprobante = new Comprobante();
        comprobante.setTipoComprobante(request.tipoComprobante());
        comprobante.setSerieComprobante(request.tipoComprobante() == TipoComprobante.BOLETA ? "B001" : "F001");
        comprobante.setNumeroComprobante(String.format("%08d",
                comprobanteRepository.countByTipoComprobante(request.tipoComprobante()) + 1));
        comprobante.setMontoTotalComprobante(venta.getTotalVenta());
        comprobante.setRucEmisor(rucEmisor);
        comprobante.setNumDocumento(request.numDocumento());
        comprobante.setNombreCliente(request.nombreCliente());
        comprobante.setRazonSocial(request.razonSocial());
        comprobante.setDireccionFiscal(request.direccionFiscal());
        comprobante.setVenta(venta);
        return comprobanteRepository.save(comprobante);
    }

    /**
     * Devuelve el PDF del comprobante. La primera vez lo escribe en disco
     * y guarda la ruta en el registro.
     */
    @Transactional
    public byte[] obtenerPdf(Integer id) {
        Comprobante comprobante = obtener(id);
        byte[] pdf = comprobantePdfService.generar(comprobante);

        if (comprobante.getPdfPath() == null) {
            comprobante.setPdfPath(comprobantePdfService.guardarEnDisco(comprobante, pdf));
            comprobanteRepository.save(comprobante);
        }
        return pdf;
    }
}
