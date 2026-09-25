package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.DatosPrueba;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.Promocion;
import com.donmanuelito.minimarket.model.enums.EstadoPromocion;
import com.donmanuelito.minimarket.model.enums.TipoPromocion;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.PromocionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private PromocionService promocionService;

    @Test
    @DisplayName("Sin promociones vigentes el descuento es cero")
    void sinPromocionesDescuentoEsCero() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(promocionRepository.findPromocionesVigentesPorProducto(eq(1), any(), any()))
                .thenReturn(List.of());

        BigDecimal descuento = promocionService.calcularDescuento(producto, 2, new BigDecimal("10.00"));

        assertThat(descuento).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Promocion por porcentaje aplica sobre el importe completo de la linea")
    void descuentoPorPorcentaje() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(promocionRepository.findPromocionesVigentesPorProducto(eq(1), any(), any()))
                .thenReturn(List.of(promocion(TipoPromocion.PORCENTAJE, "10.00")));

        // 10% de (2 x 10.00) = 2.00
        BigDecimal descuento = promocionService.calcularDescuento(producto, 2, new BigDecimal("10.00"));

        assertThat(descuento).isEqualByComparingTo(new BigDecimal("2.00"));
    }

    @Test
    @DisplayName("Promocion de monto fijo se multiplica por la cantidad")
    void descuentoPorMontoFijo() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(promocionRepository.findPromocionesVigentesPorProducto(eq(1), any(), any()))
                .thenReturn(List.of(promocion(TipoPromocion.MONTO_FIJO, "1.50")));

        // 1.50 de descuento x 3 unidades = 4.50
        BigDecimal descuento = promocionService.calcularDescuento(producto, 3, new BigDecimal("10.00"));

        assertThat(descuento).isEqualByComparingTo(new BigDecimal("4.50"));
    }

    @Test
    @DisplayName("El descuento nunca supera el importe de la linea")
    void descuentoNoSuperaImporteDeLinea() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(promocionRepository.findPromocionesVigentesPorProducto(eq(1), any(), any()))
                .thenReturn(List.of(promocion(TipoPromocion.MONTO_FIJO, "25.00")));

        // Descuento de 25.00 sobre una linea de 10.00 se recorta a 10.00
        BigDecimal descuento = promocionService.calcularDescuento(producto, 1, new BigDecimal("10.00"));

        assertThat(descuento).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    private Promocion promocion(TipoPromocion tipo, String valor) {
        Promocion promocion = new Promocion();
        promocion.setIdPromocion(1);
        promocion.setNombrePromocion("Promo test");
        promocion.setTipoPromocion(tipo);
        promocion.setDescuentoPromocion(new BigDecimal(valor));
        promocion.setFechaInicioPromo(LocalDate.now().minusDays(1));
        promocion.setFechaFinPromo(LocalDate.now().plusDays(1));
        promocion.setEstadoPromocion(EstadoPromocion.ACTIVA);
        return promocion;
    }
}
