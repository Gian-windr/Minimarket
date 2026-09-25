package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.ProductoRequest;
import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    /** Listado paginado: ?page=0&size=20&sort=nombreProducto,asc */
    @GetMapping("/paginado")
    public Page<Producto> listarPaginado(@PageableDefault(size = 20) Pageable pageable) {
        return productoService.listarPaginado(pageable);
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Integer id) {
        return productoService.obtener(id);
    }

    /** Busca por codigo de barras exacto o por nombre. */
    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam String q) {
        return productoService.buscar(q);
    }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return productoService.stockBajo();
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    /** Baja logica (estado INACTIVO). */
    @DeleteMapping("/{id}")
    public Producto eliminar(@PathVariable Integer id) {
        return productoService.eliminar(id);
    }
}
