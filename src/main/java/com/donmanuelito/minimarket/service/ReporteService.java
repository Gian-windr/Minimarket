package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.repository.DetalleVentaRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    public Map<String, Object> resumenVentas(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = ventaRepository.findByEstadoVentaAndFechaVentaBetween(
                EstadoVenta.COMPLETADA, desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));

        BigDecimal montoTotal = ventas.stream()
                .map(Venta::getTotalVenta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("desde", desde);
        resumen.put("hasta", hasta);
        resumen.put("cantidadVentas", ventas.size());
        resumen.put("montoTotal", montoTotal);
        resumen.put("ventas", ventas);
        return resumen;
    }

    public List<Map<String, Object>> productosMasVendidos(LocalDate desde, LocalDate hasta, int limite) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);
        List<Object[]> filas = detalleVentaRepository.findProductosMasVendidos(
                EstadoVenta.COMPLETADA, inicio, fin, PageRequest.of(0, limite));

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Object[] fila : filas) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idProducto", fila[0]);
            item.put("nombreProducto", fila[1]);
            item.put("cantidadVendida", fila[2]);
            item.put("montoTotal", fila[3]);
            resultado.add(item);
        }
        return resultado;
    }

    public List<Map<String, Object>> clientesFrecuentes(int limite) {
        List<Object[]> filas = ventaRepository.findClientesFrecuentes(
                EstadoVenta.COMPLETADA, PageRequest.of(0, limite));

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Object[] fila : filas) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idCliente", fila[0]);
            item.put("nombreCliente", fila[1]);
            item.put("apellidoCliente", fila[2]);
            item.put("cantidadCompras", fila[3]);
            item.put("montoTotal", fila[4]);
            resultado.add(item);
        }
        return resultado;
    }

    /** Productos activos con stock igual o por debajo del minimo. */
    public List<Producto> stockBajo() {
        return productoRepository.findStockBajo(Estado.ACTIVO);
    }
}
