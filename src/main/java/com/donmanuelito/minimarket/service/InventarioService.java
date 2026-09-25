package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.AjusteInventarioRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.MovimientoInventario;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.enums.TipoMovimiento;
import com.donmanuelito.minimarket.repository.MovimientoInventarioRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final AuthFacade authFacade;

    /**
     * Actualiza el stock del producto y deja registro en el kardex.
     * ENTRADA suma, SALIDA resta, AJUSTE aplica la cantidad con su signo.
     */
    @Transactional
    public MovimientoInventario registrarMovimiento(Producto producto, TipoMovimiento tipo, int cantidad,
                                                    String referencia, String motivo, Empleado empleado) {
        int stockAnterior = producto.getStockProducto();
        int stockNuevo = switch (tipo) {
            case ENTRADA -> stockAnterior + Math.abs(cantidad);
            case SALIDA -> stockAnterior - Math.abs(cantidad);
            case AJUSTE -> stockAnterior + cantidad;
        };
        if (stockNuevo < 0) {
            throw new BusinessException("Stock insuficiente para " + producto.getNombreProducto()
                    + " (disponible: " + stockAnterior + ")");
        }

        producto.setStockProducto(stockNuevo);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setReferencia(referencia);
        movimiento.setMotivo(motivo);
        movimiento.setEmpleado(empleado);
        return movimientoRepository.save(movimiento);
    }

    /** Ajuste manual de stock (mermas, productos vencidos, correcciones). */
    @Transactional
    public MovimientoInventario ajustar(AjusteInventarioRequest request) {
        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.idProducto()));
        return registrarMovimiento(producto, TipoMovimiento.AJUSTE, request.cantidad(),
                "AJUSTE MANUAL", request.motivo(), authFacade.empleadoActual());
    }

    public List<MovimientoInventario> kardex(Integer idProducto) {
        return movimientoRepository.findByProductoIdProductoOrderByFechaMovimientoDesc(idProducto);
    }

    public List<MovimientoInventario> listar() {
        return movimientoRepository.findAll();
    }
}
