package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.DatosPrueba;
import com.donmanuelito.minimarket.dto.DetalleDevolucionRequest;
import com.donmanuelito.minimarket.dto.DevolucionRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.*;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.TipoDevolucion;
import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import com.donmanuelito.minimarket.repository.*;
import com.donmanuelito.minimarket.security.AuthFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevolucionServiceTest {

    @Mock private DevolucionRepository devolucionRepository;
    @Mock private DetalleDevolucionRepository detalleDevolucionRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private ComprobanteRepository comprobanteRepository;
    @Mock private NotaCreditoRepository notaCreditoRepository;
    @Mock private InventarioService inventarioService;
    @Mock private AuthFacade authFacade;

    @InjectMocks
    private DevolucionService devolucionService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = DatosPrueba.empleado(1);
    }

    @Test
    @DisplayName("La devolucion cobra el precio realmente pagado por unidad y repone el stock")
    void devolucionParcialCalculaMontoProporcional() {
        Producto producto = DatosPrueba.producto(1, "12.00", 5);
        Venta venta = DatosPrueba.venta(10, empleado);
        // 4 unidades por 40.00 en total -> 10.00 efectivos por unidad
        DetalleVenta detalle = DatosPrueba.detalleVenta(100, venta, producto, 4, "40.00");

        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(detalleVentaRepository.findById(100)).thenReturn(Optional.of(detalle));
        when(detalleDevolucionRepository.totalDevueltoPorDetalleVenta(100)).thenReturn(0);
        when(devolucionRepository.save(any(Devolucion.class))).thenAnswer(invocacion -> {
            Devolucion devolucion = invocacion.getArgument(0);
            devolucion.setIdDevolucion(1);
            return devolucion;
        });
        when(comprobanteRepository.findByVentaIdVenta(10)).thenReturn(Optional.empty());

        Devolucion devolucion = devolucionService.registrar(new DevolucionRequest(
                10, "Producto danado", TipoDevolucion.PARCIAL, List.of(new DetalleDevolucionRequest(100, 2))));

        assertThat(devolucion.getMontoDevolucion()).isEqualByComparingTo("20.00");
        verify(inventarioService).registrarMovimiento(
                eq(producto), eq(TipoMovimiento.ENTRADA), eq(2), eq("DEVOLUCION #1"), eq("Producto danado"),
                eq(empleado));
        verifyNoInteractions(notaCreditoRepository);
    }

    @Test
    @DisplayName("No se puede devolver mas unidades de las vendidas")
    void devolverMasDeLoVendidoFalla() {
        Producto producto = DatosPrueba.producto(1, "12.00", 5);
        Venta venta = DatosPrueba.venta(10, empleado);
        DetalleVenta detalle = DatosPrueba.detalleVenta(100, venta, producto, 2, "24.00");

        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(detalleVentaRepository.findById(100)).thenReturn(Optional.of(detalle));
        when(detalleDevolucionRepository.totalDevueltoPorDetalleVenta(100)).thenReturn(0);

        assertThatThrownBy(() -> devolucionService.registrar(new DevolucionRequest(
                10, "Error", TipoDevolucion.PARCIAL, List.of(new DetalleDevolucionRequest(100, 3)))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("supera lo pendiente");
    }

    @Test
    @DisplayName("Lo ya devuelto antes descuenta del saldo disponible")
    void devolucionAcumuladaRespetaLoYaDevuelto() {
        Producto producto = DatosPrueba.producto(1, "12.00", 5);
        Venta venta = DatosPrueba.venta(10, empleado);
        DetalleVenta detalle = DatosPrueba.detalleVenta(100, venta, producto, 5, "60.00");

        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(detalleVentaRepository.findById(100)).thenReturn(Optional.of(detalle));
        // De 5 vendidas ya se devolvieron 4: solo queda 1 disponible
        when(detalleDevolucionRepository.totalDevueltoPorDetalleVenta(100)).thenReturn(4);

        assertThatThrownBy(() -> devolucionService.registrar(new DevolucionRequest(
                10, "Error", TipoDevolucion.PARCIAL, List.of(new DetalleDevolucionRequest(100, 2)))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("supera lo pendiente");
    }

    @Test
    @DisplayName("Una linea de otra venta no se puede devolver contra esta venta")
    void detalleDeOtraVentaFalla() {
        Producto producto = DatosPrueba.producto(1, "12.00", 5);
        Venta venta = DatosPrueba.venta(10, empleado);
        Venta otraVenta = DatosPrueba.venta(99, empleado);
        DetalleVenta detalleAjeno = DatosPrueba.detalleVenta(100, otraVenta, producto, 2, "24.00");

        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(detalleVentaRepository.findById(100)).thenReturn(Optional.of(detalleAjeno));

        assertThatThrownBy(() -> devolucionService.registrar(new DevolucionRequest(
                10, "Error", TipoDevolucion.PARCIAL, List.of(new DetalleDevolucionRequest(100, 1)))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no pertenece a la venta");
    }

    @Test
    @DisplayName("No se devuelve sobre una venta anulada")
    void devolverSobreVentaAnuladaFalla() {
        Venta venta = DatosPrueba.venta(10, empleado);
        venta.setEstadoVenta(EstadoVenta.ANULADA);
        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> devolucionService.registrar(new DevolucionRequest(
                10, "Error", TipoDevolucion.TOTAL, List.of(new DetalleDevolucionRequest(100, 1)))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("venta anulada");
    }

    @Test
    @DisplayName("Si la venta tiene comprobante se genera la nota de credito")
    void devolucionConComprobanteGeneraNotaDeCredito() {
        Producto producto = DatosPrueba.producto(1, "12.00", 5);
        Venta venta = DatosPrueba.venta(10, empleado);
        DetalleVenta detalle = DatosPrueba.detalleVenta(100, venta, producto, 2, "24.00");
        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(50);

        when(ventaRepository.findById(10)).thenReturn(Optional.of(venta));
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(detalleVentaRepository.findById(100)).thenReturn(Optional.of(detalle));
        when(detalleDevolucionRepository.totalDevueltoPorDetalleVenta(100)).thenReturn(0);
        when(devolucionRepository.save(any(Devolucion.class))).thenAnswer(invocacion -> {
            Devolucion devolucion = invocacion.getArgument(0);
            devolucion.setIdDevolucion(2);
            return devolucion;
        });
        when(comprobanteRepository.findByVentaIdVenta(10)).thenReturn(Optional.of(comprobante));

        devolucionService.registrar(new DevolucionRequest(
                10, "Devolucion total", TipoDevolucion.TOTAL, List.of(new DetalleDevolucionRequest(100, 2))));

        verify(notaCreditoRepository).save(any(NotaCredito.class));
    }
}
