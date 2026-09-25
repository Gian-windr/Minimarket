package com.donmanuelito.minimarket.dto;

import com.donmanuelito.minimarket.model.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Resumen operativo para la pantalla principal del sistema. */
public record DashboardResponse(
        long ventasHoy,
        BigDecimal montoHoy,
        long ventasMes,
        BigDecimal montoMes,
        long productosActivos,
        long productosStockBajo,
        List<Producto> alertasStock,
        boolean cajaAbierta,
        Integer idCajaAbierta,
        BigDecimal montoInicialCaja,
        List<Map<String, Object>> topProductosMes) {
}
