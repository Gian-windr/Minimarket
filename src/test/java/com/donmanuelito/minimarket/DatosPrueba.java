package com.donmanuelito.minimarket;

import com.donmanuelito.minimarket.model.*;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import com.donmanuelito.minimarket.model.enums.MetodoPago;

import java.math.BigDecimal;

/** Constructores de entidades para las pruebas. */
public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static Empleado empleado(Integer id) {
        Rol rol = new Rol("CAJERO");
        rol.setIdRol(2);

        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(id);
        empleado.setNombreEmpleado("Ana");
        empleado.setApellidoEmpleado("Torres");
        empleado.setRol(rol);
        return empleado;
    }

    public static Producto producto(Integer id, String precio, int stock) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombreProducto("Producto " + id);
        producto.setPrecioProducto(new BigDecimal(precio));
        producto.setStockProducto(stock);
        producto.setStockMinimo(5);
        producto.setEstadoProducto(Estado.ACTIVO);
        return producto;
    }

    public static Venta venta(Integer id, Empleado empleado) {
        Venta venta = new Venta();
        venta.setIdVenta(id);
        venta.setEmpleado(empleado);
        venta.setMetodoPago(MetodoPago.EFECTIVO);
        venta.setEstadoVenta(EstadoVenta.COMPLETADA);
        venta.setSubtotalVenta(new BigDecimal("100.00"));
        venta.setIgvVenta(new BigDecimal("18.00"));
        venta.setTotalVenta(new BigDecimal("118.00"));
        return venta;
    }

    /** Linea de venta con su importe ya calculado. */
    public static DetalleVenta detalleVenta(Integer id, Venta venta, Producto producto,
                                            int cantidad, String subtotal) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(id);
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidadDetalle(cantidad);
        detalle.setPrecioUnitarioDetalle(producto.getPrecioProducto());
        detalle.setSubtotalDetalle(new BigDecimal(subtotal));
        venta.getDetalles().add(detalle);
        return detalle;
    }
}
