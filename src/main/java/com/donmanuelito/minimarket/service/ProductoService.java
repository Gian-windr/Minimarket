package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.ProductoRequest;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.repository.CategoriaProductoRepository;
import com.donmanuelito.minimarket.repository.ProductoRepository;
import com.donmanuelito.minimarket.repository.TipoProductoRepository;
import com.donmanuelito.minimarket.repository.UnidadMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final TipoProductoRepository tipoProductoRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Page<Producto> listarPaginado(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Producto obtener(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    /** Busca por codigo de barras exacto o por coincidencia en el nombre. */
    public List<Producto> buscar(String q) {
        return productoRepository.findByCodigoBarras(q)
                .map(List::of)
                .orElseGet(() -> productoRepository.findByNombreProductoContainingIgnoreCase(q));
    }

    public List<Producto> stockBajo() {
        return productoRepository.findStockBajo(Estado.ACTIVO);
    }

    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto();
        aplicar(request, producto);
        producto.setStockProducto(request.stockProducto());
        return productoRepository.save(producto);
    }

    public Producto actualizar(Integer id, ProductoRequest request) {
        Producto producto = obtener(id);
        aplicar(request, producto);
        // El stock no se edita aqui: usar compras o ajustes de inventario
        return productoRepository.save(producto);
    }

    /** Baja logica: el producto deja de estar disponible para la venta. */
    public Producto eliminar(Integer id) {
        Producto producto = obtener(id);
        producto.setEstadoProducto(Estado.INACTIVO);
        return productoRepository.save(producto);
    }

    private void aplicar(ProductoRequest request, Producto producto) {
        producto.setNombreProducto(request.nombreProducto());
        producto.setDescripcionProducto(request.descripcionProducto());
        producto.setPrecioProducto(request.precioProducto());
        producto.setPrecioCompra(request.precioCompra());
        producto.setStockMinimo(request.stockMinimo());
        producto.setTipoCorte(request.tipoCorte() != null && request.tipoCorte());
        producto.setImagen(request.imagen());
        producto.setCodigoBarras(request.codigoBarras());

        if (request.idTipoProducto() != null) {
            producto.setTipoProducto(tipoProductoRepository.findById(request.idTipoProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", request.idTipoProducto())));
        }
        if (request.idCategoriaProducto() != null) {
            producto.setCategoriaProducto(categoriaProductoRepository.findById(request.idCategoriaProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", request.idCategoriaProducto())));
        }
        if (request.idUnidadMedida() != null) {
            producto.setUnidadMedida(unidadMedidaRepository.findById(request.idUnidadMedida())
                    .orElseThrow(() -> new ResourceNotFoundException("UnidadMedida", request.idUnidadMedida())));
        }
    }
}
