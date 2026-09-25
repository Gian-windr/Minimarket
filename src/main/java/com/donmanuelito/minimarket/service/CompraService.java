package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.CompraRequest;
import com.donmanuelito.minimarket.dto.DetalleCompraRequest;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.*;
import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import com.donmanuelito.minimarket.repository.CompraRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.ProveedorRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final AuthFacade authFacade;

    public List<Compra> listar() {
        return compraRepository.findAll();
    }

    public Compra obtener(Integer id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
    }

    /** Registra el ingreso de mercaderia: suma stock y actualiza el precio de compra. */
    @Transactional
    public Compra registrar(CompraRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.idProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.idProveedor()));
        Empleado empleado = authFacade.empleadoActual();

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setEmpleado(empleado);
        compra.setNumeroFacturaProveedor(request.numeroFacturaProveedor());

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCompraRequest item : request.items()) {
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", item.idProducto()));

            BigDecimal subtotal = item.precioCompraUnitario()
                    .multiply(BigDecimal.valueOf(item.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleCompra detalle = new DetalleCompra();
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioCompraUnitario(item.precioCompraUnitario());
            detalle.setSubtotal(subtotal);
            detalle.setCompra(compra);
            compra.getDetalles().add(detalle);

            total = total.add(subtotal);
        }
        compra.setTotalCompra(total);

        Compra guardada = compraRepository.save(compra);

        for (DetalleCompra detalle : guardada.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setPrecioCompra(detalle.getPrecioCompraUnitario());
            inventarioService.registrarMovimiento(producto, TipoMovimiento.ENTRADA, detalle.getCantidad(),
                    "COMPRA #" + guardada.getIdCompra(), null, empleado);
        }

        return guardada;
    }
}
