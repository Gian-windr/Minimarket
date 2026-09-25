package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.PromocionRequest;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.Promocion;
import com.donmanuelito.minimarket.model.enums.EstadoPromocion;
import com.donmanuelito.minimarket.model.enums.TipoPromocion;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocionService {

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    public List<Promocion> listar() {
        return promocionRepository.findAll();
    }

    public Promocion obtener(Integer id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promocion", id));
    }

    public Promocion crear(PromocionRequest request) {
        Promocion promocion = new Promocion();
        aplicar(request, promocion);
        return promocionRepository.save(promocion);
    }

    public Promocion actualizar(Integer id, PromocionRequest request) {
        Promocion promocion = obtener(id);
        aplicar(request, promocion);
        return promocionRepository.save(promocion);
    }

    public Promocion cambiarEstado(Integer id, EstadoPromocion estado) {
        Promocion promocion = obtener(id);
        promocion.setEstadoPromocion(estado);
        return promocionRepository.save(promocion);
    }

    /**
     * Descuento total para una linea de venta segun la primera promocion
     * vigente del producto. Nunca supera el importe de la linea.
     */
    public BigDecimal calcularDescuento(Producto producto, int cantidad, BigDecimal precioUnitario) {
        List<Promocion> vigentes = promocionRepository.findPromocionesVigentesPorProducto(
                producto.getIdProducto(), EstadoPromocion.ACTIVA, LocalDate.now());
        if (vigentes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Promocion promocion = vigentes.get(0);
        BigDecimal base = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal descuento = promocion.getTipoPromocion() == TipoPromocion.PORCENTAJE
                ? base.multiply(promocion.getDescuentoPromocion())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : promocion.getDescuentoPromocion().multiply(BigDecimal.valueOf(cantidad));
        return descuento.min(base);
    }

    private void aplicar(PromocionRequest request, Promocion promocion) {
        promocion.setNombrePromocion(request.nombrePromocion());
        promocion.setDescripcionPromocion(request.descripcionPromocion());
        promocion.setTipoPromocion(request.tipoPromocion());
        promocion.setDescuentoPromocion(request.descuentoPromocion());
        promocion.setFechaInicioPromo(request.fechaInicioPromo());
        promocion.setFechaFinPromo(request.fechaFinPromo());

        List<Producto> productos = new ArrayList<>();
        if (request.idProductos() != null) {
            for (Integer idProducto : request.idProductos()) {
                productos.add(productoRepository.findById(idProducto)
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", idProducto)));
            }
        }
        promocion.setProductos(productos);
    }
}
