package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.DatosPrueba;
import com.donmanuelito.minimarket.dto.ComprobanteRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Comprobante;
import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.TipoComprobante;
import com.donmanuelito.minimarket.repository.ComprobanteRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComprobanteServiceTest {

    @Mock private ComprobanteRepository comprobanteRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private ComprobantePdfService comprobantePdfService;

    @InjectMocks
    private ComprobanteService comprobanteService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = DatosPrueba.venta(1, DatosPrueba.empleado(1));
        ReflectionTestUtils.setField(comprobanteService, "rucEmisor", "20000000001");
    }

    @Test
    @DisplayName("La boleta usa serie B001 y correlativo de 8 digitos")
    void emitirBoletaAsignaSerieYCorrelativo() {
        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));
        when(comprobanteRepository.existsByVentaIdVenta(1)).thenReturn(false);
        when(comprobanteRepository.countByTipoComprobante(TipoComprobante.BOLETA)).thenReturn(5L);
        when(comprobanteRepository.save(any(Comprobante.class))).thenAnswer(i -> i.getArgument(0));

        Comprobante comprobante = comprobanteService.emitir(new ComprobanteRequest(
                1, TipoComprobante.BOLETA, "12345678", "Juan Perez", null, null));

        assertThat(comprobante.getSerieComprobante()).isEqualTo("B001");
        assertThat(comprobante.getNumeroComprobante()).isEqualTo("00000006");
        assertThat(comprobante.getRucEmisor()).isEqualTo("20000000001");
        assertThat(comprobante.getMontoTotalComprobante()).isEqualByComparingTo("118.00");
    }

    @Test
    @DisplayName("La factura exige RUC y razon social del cliente")
    void emitirFacturaSinRazonSocialFalla() {
        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));
        when(comprobanteRepository.existsByVentaIdVenta(1)).thenReturn(false);

        assertThatThrownBy(() -> comprobanteService.emitir(new ComprobanteRequest(
                1, TipoComprobante.FACTURA, "20123456789", null, null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("RUC y razon social");

        verify(comprobanteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Una venta no puede tener dos comprobantes")
    void emitirDosVecesFalla() {
        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));
        when(comprobanteRepository.existsByVentaIdVenta(1)).thenReturn(true);

        assertThatThrownBy(() -> comprobanteService.emitir(new ComprobanteRequest(
                1, TipoComprobante.BOLETA, "12345678", "Juan Perez", null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya tiene comprobante");
    }

    @Test
    @DisplayName("No se emite comprobante de una venta anulada")
    void emitirDeVentaAnuladaFalla() {
        venta.setEstadoVenta(EstadoVenta.ANULADA);
        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> comprobanteService.emitir(new ComprobanteRequest(
                1, TipoComprobante.BOLETA, "12345678", "Juan Perez", null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ventas completadas");
    }
}
