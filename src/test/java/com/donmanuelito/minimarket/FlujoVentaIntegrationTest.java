package com.donmanuelito.minimarket;

import com.donmanuelito.minimarket.dto.ComprobanteRequest;
import com.donmanuelito.minimarket.dto.DetalleVentaRequest;
import com.donmanuelito.minimarket.dto.VentaRequest;
import com.donmanuelito.minimarket.model.*;
import com.donmanuelito.minimarket.model.enums.MetodoPago;
import com.donmanuelito.minimarket.model.enums.TipoComprobante;
import com.donmanuelito.minimarket.repository.*;
import com.donmanuelito.minimarket.security.AuthFacade;
import com.donmanuelito.minimarket.service.ComprobanteService;
import com.donmanuelito.minimarket.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Ejercita el flujo real contra una base H2: registrar venta, descontar stock,
 * dejar el movimiento en el kardex y emitir el comprobante con su PDF.
 */
@SpringBootTest
@Transactional
class FlujoVentaIntegrationTest {

    @Autowired private VentaService ventaService;
    @Autowired private ComprobanteService comprobanteService;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private MovimientoInventarioRepository movimientoRepository;

    @MockitoBean
    private AuthFacade authFacade;

    private Empleado cajero;

    @BeforeEach
    void setUp() {
        cajero = empleadoRepository.findAll().stream().findFirst().orElseGet(this::crearCajero);
        when(authFacade.empleadoActual()).thenReturn(cajero);
    }

    @Test
    @DisplayName("Vender descuenta el stock y registra el movimiento en el kardex")
    void ventaDescuentaStockYRegistraKardex() {
        Producto producto = crearProducto("Gaseosa 500ml", "5.90", 50);

        Venta venta = ventaService.registrar(new VentaRequest(
                MetodoPago.EFECTIVO, null, List.of(new DetalleVentaRequest(producto.getIdProducto(), 10))));

        // 10 x 5.90 = 59.00 con IGV incluido -> IGV 9.00, base 50.00
        assertThat(venta.getIdVenta()).isNotNull();
        assertThat(venta.getTotalVenta()).isEqualByComparingTo("59.00");
        assertThat(venta.getIgvVenta()).isEqualByComparingTo("9.00");
        assertThat(venta.getSubtotalVenta()).isEqualByComparingTo("50.00");

        Producto actualizado = productoRepository.findById(producto.getIdProducto()).orElseThrow();
        assertThat(actualizado.getStockProducto()).isEqualTo(40);

        List<MovimientoInventario> kardex =
                movimientoRepository.findByProductoIdProductoOrderByFechaMovimientoDesc(producto.getIdProducto());
        assertThat(kardex).hasSize(1);
        assertThat(kardex.get(0).getStockAnterior()).isEqualTo(50);
        assertThat(kardex.get(0).getStockNuevo()).isEqualTo(40);
    }

    @Test
    @DisplayName("Anular la venta devuelve el stock y deja rastro en el kardex")
    void anularVentaRestituyeStock() {
        Producto producto = crearProducto("Arroz 1kg", "4.50", 30);

        Venta venta = ventaService.registrar(new VentaRequest(
                MetodoPago.EFECTIVO, null, List.of(new DetalleVentaRequest(producto.getIdProducto(), 6))));
        assertThat(productoRepository.findById(producto.getIdProducto()).orElseThrow()
                .getStockProducto()).isEqualTo(24);

        ventaService.anular(venta.getIdVenta());

        assertThat(productoRepository.findById(producto.getIdProducto()).orElseThrow()
                .getStockProducto()).isEqualTo(30);
        assertThat(movimientoRepository.findByProductoIdProductoOrderByFechaMovimientoDesc(
                producto.getIdProducto())).hasSize(2);
    }

    @Test
    @DisplayName("El comprobante emitido genera un PDF valido")
    void emitirComprobanteGeneraPdf() {
        Producto producto = crearProducto("Aceite 1L", "9.90", 20);
        Venta venta = ventaService.registrar(new VentaRequest(
                MetodoPago.EFECTIVO, null, List.of(new DetalleVentaRequest(producto.getIdProducto(), 2))));

        Comprobante comprobante = comprobanteService.emitir(new ComprobanteRequest(
                venta.getIdVenta(), TipoComprobante.BOLETA, "12345678", "Juan Perez", null, null));

        assertThat(comprobante.getSerieComprobante()).isEqualTo("B001");
        assertThat(comprobante.getNumeroComprobante()).hasSize(8);

        byte[] pdf = comprobanteService.obtenerPdf(comprobante.getIdComprobante());

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    private Producto crearProducto(String nombre, String precio, int stock) {
        Producto producto = new Producto();
        producto.setNombreProducto(nombre);
        producto.setPrecioProducto(new BigDecimal(precio));
        producto.setStockProducto(stock);
        producto.setStockMinimo(5);
        return productoRepository.save(producto);
    }

    private Empleado crearCajero() {
        Rol rol = rolRepository.findByNombreRol("CAJERO").orElseGet(() -> rolRepository.save(new Rol("CAJERO")));
        Empleado empleado = new Empleado();
        empleado.setNombreEmpleado("Test");
        empleado.setApellidoEmpleado("Cajero");
        empleado.setRol(rol);
        return empleadoRepository.save(empleado);
    }
}
