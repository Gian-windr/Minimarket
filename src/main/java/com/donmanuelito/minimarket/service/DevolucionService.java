package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.DetalleDevolucionRequest;
import com.donmanuelito.minimarket.dto.DevolucionRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.*;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import com.donmanuelito.minimarket.repository.*;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final DetalleDevolucionRepository detalleDevolucionRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final NotaCreditoRepository notaCreditoRepository;
    private final InventarioService inventarioService;
    private final AuthFacade authFacade;

    public List<Devolucion> listar() {
        return devolucionRepository.findAll();
    }

    public Devolucion obtener(Integer id) {
        return devolucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolucion", id));
    }

    /**
     * Registra la devolucion, restituye stock y, si la venta tiene
     * comprobante, genera automaticamente la nota de credito.
     */
    @Transactional
    public Devolucion registrar(DevolucionRequest request) {
        Venta venta = ventaRepository.findById(request.idVenta())
                .orElseThrow(() -> new ResourceNotFoundException("Venta", request.idVenta()));
        if (venta.getEstadoVenta() == EstadoVenta.ANULADA) {
            throw new BusinessException("No se puede devolver sobre una venta anulada");
        }

        Empleado empleado = authFacade.empleadoActual();

        Devolucion devolucion = new Devolucion();
        devolucion.setVenta(venta);
        devolucion.setMotivoDevolucion(request.motivo());
        devolucion.setTipoDevolucion(request.tipoDevolucion());

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleDevolucionRequest item : request.items()) {
            DetalleVenta detalleVenta = detalleVentaRepository.findById(item.idDetalleVenta())
                    .orElseThrow(() -> new ResourceNotFoundException("DetalleVenta", item.idDetalleVenta()));

            if (!detalleVenta.getVenta().getIdVenta().equals(venta.getIdVenta())) {
                throw new BusinessException("El detalle #" + item.idDetalleVenta()
                        + " no pertenece a la venta #" + venta.getIdVenta());
            }

            int yaDevuelto = detalleDevolucionRepository
                    .totalDevueltoPorDetalleVenta(detalleVenta.getIdDetalleVenta());
            int disponible = detalleVenta.getCantidadDetalle() - yaDevuelto;
            if (item.cantidad() > disponible) {
                throw new BusinessException("Cantidad a devolver (" + item.cantidad()
                        + ") supera lo pendiente (" + disponible + ") del producto "
                        + detalleVenta.getProducto().getNombreProducto());
            }

            // Precio efectivo pagado por unidad (con descuento prorrateado)
            BigDecimal precioEfectivo = detalleVenta.getSubtotalDetalle()
                    .divide(BigDecimal.valueOf(detalleVenta.getCantidadDetalle()), 2, RoundingMode.HALF_UP);
            BigDecimal monto = precioEfectivo.multiply(BigDecimal.valueOf(item.cantidad()));

            DetalleDevolucion detalle = new DetalleDevolucion();
            detalle.setDevolucion(devolucion);
            detalle.setProducto(detalleVenta.getProducto());
            detalle.setDetalleVenta(detalleVenta);
            detalle.setCantidadDevolucion(item.cantidad());
            detalle.setMontoDevolucion(monto);
            devolucion.getDetalles().add(detalle);

            total = total.add(monto);
        }
        devolucion.setMontoDevolucion(total);

        Devolucion guardada = devolucionRepository.save(devolucion);

        for (DetalleDevolucion detalle : guardada.getDetalles()) {
            inventarioService.registrarMovimiento(detalle.getProducto(), TipoMovimiento.ENTRADA,
                    detalle.getCantidadDevolucion(), "DEVOLUCION #" + guardada.getIdDevolucion(),
                    request.motivo(), empleado);
        }

        comprobanteRepository.findByVentaIdVenta(venta.getIdVenta()).ifPresent(comprobante -> {
            NotaCredito nota = new NotaCredito();
            nota.setComprobante(comprobante);
            nota.setVenta(venta);
            nota.setMontoTotalNota(guardada.getMontoDevolucion());
            nota.setMotivo(request.motivo());
            notaCreditoRepository.save(nota);
        });

        return guardada;
    }
}
