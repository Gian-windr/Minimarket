package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.DatosPrueba;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.MovimientoInventario;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import com.donmanuelito.minimarket.repository.MovimientoInventarioRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private MovimientoInventarioRepository movimientoRepository;
    @Mock private AuthFacade authFacade;

    @InjectMocks
    private InventarioService inventarioService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = DatosPrueba.empleado(1);
    }

    @Test
    @DisplayName("Una entrada suma stock y deja el movimiento con el saldo anterior y nuevo")
    void entradaSumaStock() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoInventario movimiento = inventarioService.registrarMovimiento(
                producto, TipoMovimiento.ENTRADA, 15, "COMPRA #1", null, empleado);

        assertThat(producto.getStockProducto()).isEqualTo(35);
        assertThat(movimiento.getStockAnterior()).isEqualTo(20);
        assertThat(movimiento.getStockNuevo()).isEqualTo(35);
        verify(productoRepository).save(producto);
    }

    @Test
    @DisplayName("Una salida resta stock")
    void salidaRestaStock() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.registrarMovimiento(producto, TipoMovimiento.SALIDA, 8, "VENTA #1", null, empleado);

        assertThat(producto.getStockProducto()).isEqualTo(12);
    }

    @Test
    @DisplayName("Un ajuste negativo descuenta stock por merma")
    void ajusteNegativoDescuentaStock() {
        Producto producto = DatosPrueba.producto(1, "10.00", 20);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(i -> i.getArgument(0));

        inventarioService.registrarMovimiento(
                producto, TipoMovimiento.AJUSTE, -3, "AJUSTE MANUAL", "Productos vencidos", empleado);

        assertThat(producto.getStockProducto()).isEqualTo(17);
    }

    @Test
    @DisplayName("Ningun movimiento puede dejar el stock en negativo")
    void movimientoQueDejaStockNegativoFalla() {
        Producto producto = DatosPrueba.producto(1, "10.00", 2);

        assertThatThrownBy(() -> inventarioService.registrarMovimiento(
                producto, TipoMovimiento.SALIDA, 5, "VENTA #1", null, empleado))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(producto.getStockProducto()).isEqualTo(2);
        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }
}
