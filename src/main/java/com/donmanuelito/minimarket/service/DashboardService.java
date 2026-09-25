package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.DashboardResponse;
import com.donmanuelito.minimarket.model.Caja;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.model.enums.EstadoCaja;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.repository.CajaRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    /** Cantidad de productos con stock bajo que se muestran en el panel. */
    private static final int MAX_ALERTAS = 5;
    private static final int MAX_TOP_PRODUCTOS = 5;

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final CajaRepository cajaRepository;
    private final ReporteService reporteService;
    private final AuthFacade authFacade;

    public DashboardResponse resumen() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<Venta> ventasHoy = ventasEntre(hoy, hoy);
        List<Venta> ventasMes = ventasEntre(inicioMes, hoy);

        List<Producto> stockBajo = productoRepository.findStockBajo(Estado.ACTIVO);
        List<Producto> alertas = stockBajo.size() > MAX_ALERTAS ? stockBajo.subList(0, MAX_ALERTAS) : stockBajo;

        Optional<Caja> caja = cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(
                authFacade.empleadoActual().getIdEmpleado(), EstadoCaja.ABIERTA);

        List<Map<String, Object>> topProductos =
                reporteService.productosMasVendidos(inicioMes, hoy, MAX_TOP_PRODUCTOS);

        return new DashboardResponse(
                ventasHoy.size(),
                sumarTotales(ventasHoy),
                ventasMes.size(),
                sumarTotales(ventasMes),
                productoRepository.countByEstadoProducto(Estado.ACTIVO),
                stockBajo.size(),
                alertas,
                caja.isPresent(),
                caja.map(Caja::getIdCaja).orElse(null),
                caja.map(Caja::getMontoInicial).orElse(null),
                topProductos);
    }

    private List<Venta> ventasEntre(LocalDate desde, LocalDate hasta) {
        return ventaRepository.findByEstadoVentaAndFechaVentaBetween(
                EstadoVenta.COMPLETADA, desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
    }

    private BigDecimal sumarTotales(List<Venta> ventas) {
        return ventas.stream()
                .map(Venta::getTotalVenta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
