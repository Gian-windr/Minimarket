package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.DetalleVentaRequest;
import com.donmanuelito.minimarket.dto.VentaRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.*;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.model.enums.EstadoCaja;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import com.donmanuelito.minimarket.repository.CajaRepository;
import com.donmanuelito.minimarket.repository.ClienteRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final CajaRepository cajaRepository;
    private final PromocionService promocionService;
    private final InventarioService inventarioService;
    private final AuditoriaService auditoriaService;
    private final AuthFacade authFacade;

    @Value("${app.igv}")
    private BigDecimal igv;

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    public Page<Venta> listarPaginado(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    public Venta obtener(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
    }

    public List<Venta> listarPorFechas(LocalDate desde, LocalDate hasta) {
        return ventaRepository.findByEstadoVentaAndFechaVentaBetween(
                EstadoVenta.COMPLETADA, desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
    }

    @Transactional
    public Venta registrar(VentaRequest request) {
        Empleado empleado = authFacade.empleadoActual();

        Venta venta = new Venta();
        venta.setMetodoPago(request.metodoPago());
        venta.setEmpleado(empleado);

        if (request.idCliente() != null) {
            Cliente cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.idCliente()));
            venta.setCliente(cliente);
        }

        // Se asocia a la caja abierta del empleado, si la tiene
        cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(
                empleado.getIdEmpleado(), EstadoCaja.ABIERTA).ifPresent(venta::setCaja);

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVentaRequest item : request.items()) {
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", item.idProducto()));

            if (producto.getEstadoProducto() != Estado.ACTIVO) {
                throw new BusinessException("El producto no esta activo: " + producto.getNombreProducto());
            }
            if (producto.getStockProducto() < item.cantidad()) {
                throw new BusinessException("Stock insuficiente para " + producto.getNombreProducto()
                        + " (disponible: " + producto.getStockProducto() + ")");
            }

            BigDecimal precio = producto.getPrecioProducto();
            BigDecimal descuento = promocionService.calcularDescuento(producto, item.cantidad(), precio);
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.cantidad()))
                    .subtract(descuento).setScale(2, RoundingMode.HALF_UP);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidadDetalle(item.cantidad());
            detalle.setPrecioUnitarioDetalle(precio);
            detalle.setDescuentoAplicado(descuento);
            detalle.setSubtotalDetalle(subtotal);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);

            total = total.add(subtotal);
        }

        // Los precios incluyen IGV: se desglosa del total
        BigDecimal montoIgv = total.multiply(igv)
                .divide(BigDecimal.ONE.add(igv), 2, RoundingMode.HALF_UP);
        venta.setTotalVenta(total.setScale(2, RoundingMode.HALF_UP));
        venta.setIgvVenta(montoIgv);
        venta.setSubtotalVenta(venta.getTotalVenta().subtract(montoIgv));

        Venta guardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : guardada.getDetalles()) {
            inventarioService.registrarMovimiento(detalle.getProducto(), TipoMovimiento.SALIDA,
                    detalle.getCantidadDetalle(), "VENTA #" + guardada.getIdVenta(), null, empleado);
        }

        return guardada;
    }

    /** Anula la venta y restituye el stock de todos sus productos. */
    @Transactional
    public Venta anular(Integer id) {
        Venta venta = obtener(id);
        if (venta.getEstadoVenta() == EstadoVenta.ANULADA) {
            throw new BusinessException("La venta ya esta anulada");
        }

        Empleado empleado = authFacade.empleadoActual();
        for (DetalleVenta detalle : venta.getDetalles()) {
            inventarioService.registrarMovimiento(detalle.getProducto(), TipoMovimiento.ENTRADA,
                    detalle.getCantidadDetalle(), "ANULACION VENTA #" + id, null, empleado);
        }

        venta.setEstadoVenta(EstadoVenta.ANULADA);
        Venta anulada = ventaRepository.save(venta);
        auditoriaService.registrar("ANULAR_VENTA", "venta", "Venta #" + id + " anulada");
        return anulada;
    }
}
