package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.DatosPrueba;
import com.donmanuelito.minimarket.dto.DetalleVentaRequest;
import com.donmanuelito.minimarket.dto.VentaRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.model.enums.*;
import com.donmanuelito.minimarket.repository.CajaRepository;
import com.donmanuelito.minimarket.repository.ClienteRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.VentaRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private CajaRepository cajaRepository;
    @Mock private PromocionService promocionService;
    @Mock private InventarioService inventarioService;
    @Mock private AuditoriaService auditoriaService;
    @Mock private AuthFacade authFacade;

    @InjectMocks
    private VentaService ventaService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = DatosPrueba.empleado(1);
        ReflectionTestUtils.setField(ventaService, "igv", new BigDecimal("0.18"));
    }

    @Test
    @DisplayName("La venta desglosa el IGV del total y descuenta el stock vendido")
    void registrarCalculaIgvYDescuentaStock() {
        Producto producto = DatosPrueba.producto(1, "59.00", 10);
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(1, EstadoCaja.ABIERTA))
                .thenReturn(Optional.empty());
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(promocionService.calcularDescuento(eq(producto), eq(2), any())).thenReturn(BigDecimal.ZERO);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocacion -> {
            Venta venta = invocacion.getArgument(0);
            venta.setIdVenta(1);
            return venta;
        });

        Venta venta = ventaService.registrar(
                new VentaRequest(MetodoPago.EFECTIVO, null, List.of(new DetalleVentaRequest(1, 2))));

        // 2 x 59.00 = 118.00 con IGV incluido -> IGV 18.00, base 100.00
        assertThat(venta.getTotalVenta()).isEqualByComparingTo("118.00");
        assertThat(venta.getIgvVenta()).isEqualByComparingTo("18.00");
        assertThat(venta.getSubtotalVenta()).isEqualByComparingTo("100.00");
        assertThat(venta.getDetalles()).hasSize(1);

        verify(inventarioService).registrarMovimiento(
                eq(producto), eq(TipoMovimiento.SALIDA), eq(2), eq("VENTA #1"), isNull(), eq(empleado));
    }

    @Test
    @DisplayName("No se puede vender mas unidades de las que hay en stock")
    void registrarConStockInsuficienteFalla() {
        Producto producto = DatosPrueba.producto(1, "10.00", 1);
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(1, EstadoCaja.ABIERTA))
                .thenReturn(Optional.empty());
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> ventaService.registrar(
                new VentaRequest(MetodoPago.EFECTIVO, null, List.of(new DetalleVentaRequest(1, 5)))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Un producto dado de baja no se puede vender")
    void registrarConProductoInactivoFalla() {
        Producto producto = DatosPrueba.producto(1, "10.00", 50);
        producto.setEstadoProducto(Estado.INACTIVO);
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(cajaRepository.findFirstByEmpleadoIdEmpleadoAndEstadoCaja(1, EstadoCaja.ABIERTA))
                .thenReturn(Optional.empty());
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> ventaService.registrar(
                new VentaRequest(MetodoPago.EFECTIVO, null, List.of(new DetalleVentaRequest(1, 1)))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no esta activo");
    }

    @Test
    @DisplayName("Anular una venta devuelve las unidades al inventario")
    void anularRestituyeStock() {
        Producto producto = DatosPrueba.producto(1, "10.00", 7);
        Venta venta = DatosPrueba.venta(5, empleado);
        DatosPrueba.detalleVenta(1, venta, producto, 3, "30.00");

        when(ventaRepository.findById(5)).thenReturn(Optional.of(venta));
        when(authFacade.empleadoActual()).thenReturn(empleado);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        Venta anulada = ventaService.anular(5);

        assertThat(anulada.getEstadoVenta()).isEqualTo(EstadoVenta.ANULADA);
        verify(inventarioService).registrarMovimiento(
                eq(producto), eq(TipoMovimiento.ENTRADA), eq(3), eq("ANULACION VENTA #5"), isNull(), eq(empleado));
    }

    @Test
    @DisplayName("Una venta ya anulada no se puede anular de nuevo")
    void anularVentaYaAnuladaFalla() {
        Venta venta = DatosPrueba.venta(5, empleado);
        venta.setEstadoVenta(EstadoVenta.ANULADA);
        when(ventaRepository.findById(5)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> ventaService.anular(5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya esta anulada");
    }
}
