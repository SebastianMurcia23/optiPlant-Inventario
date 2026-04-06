package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.CrearCategoriaDTO;
import com.inventario.dto.request.CrearProductoDTO;
import com.inventario.dto.request.CrearProveedorDTO;
import com.inventario.dto.request.CrearUnidadMedidaDTO;
import com.inventario.dto.response.ProductoDTO;
import com.inventario.dto.response.ProveedorDTO;
import com.inventario.model.Categoria;
import com.inventario.model.UnidadMedida;
import com.inventario.service.interfaces.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catálogo", description = "Categorías, unidades, proveedores y productos")
public class CatalogoControlador {

    private final CategoriaServicio categoriaServicio;
    private final UnidadMedidaServicio unidadMedidaServicio;
    private final ProveedorServicio proveedorServicio;
    private final ProductoServicio productoServicio;

    // ── CATEGORÍAS ────────────────────────────────────────

    @PostMapping("/categorias")
    @Operation(summary = "Crear categoría")
    public ResponseEntity<MensajeDTO<Categoria>> crearCategoria(
            @Valid @RequestBody CrearCategoriaDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, categoriaServicio.crear(dto)));
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Editar categoría")
    public ResponseEntity<MensajeDTO<Categoria>> editarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CrearCategoriaDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, categoriaServicio.editar(id, dto)));
    }

    @DeleteMapping("/categorias/{id}")
    @Operation(summary = "Desactivar categoría")
    public ResponseEntity<MensajeDTO<String>> desactivarCategoria(
            @PathVariable Long id) throws Exception {
        categoriaServicio.desactivar(id);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Categoría desactivada"));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías activas")
    public ResponseEntity<MensajeDTO<List<Categoria>>> listarCategorias() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, categoriaServicio.listarActivas()));
    }

    // ── UNIDADES DE MEDIDA ────────────────────────────────

    @PostMapping("/unidades")
    @Operation(summary = "Crear unidad de medida")
    public ResponseEntity<MensajeDTO<UnidadMedida>> crearUnidad(
            @Valid @RequestBody CrearUnidadMedidaDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, unidadMedidaServicio.crear(dto)));
    }

    @PutMapping("/unidades/{id}")
    @Operation(summary = "Editar unidad de medida")
    public ResponseEntity<MensajeDTO<UnidadMedida>> editarUnidad(
            @PathVariable Long id,
            @Valid @RequestBody CrearUnidadMedidaDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, unidadMedidaServicio.editar(id, dto)));
    }

    @GetMapping("/unidades")
    @Operation(summary = "Listar todas las unidades")
    public ResponseEntity<MensajeDTO<List<UnidadMedida>>> listarUnidades() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, unidadMedidaServicio.listarTodas()));
    }

    // ── PROVEEDORES ───────────────────────────────────────

    @PostMapping("/proveedores")
    @Operation(summary = "Crear proveedor")
    public ResponseEntity<MensajeDTO<ProveedorDTO>> crearProveedor(
            @Valid @RequestBody CrearProveedorDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, proveedorServicio.crear(dto)));
    }

    @PutMapping("/proveedores/{id}")
    @Operation(summary = "Editar proveedor")
    public ResponseEntity<MensajeDTO<ProveedorDTO>> editarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody CrearProveedorDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, proveedorServicio.editar(id, dto)));
    }

    @DeleteMapping("/proveedores/{id}")
    @Operation(summary = "Desactivar proveedor")
    public ResponseEntity<MensajeDTO<String>> desactivarProveedor(
            @PathVariable Long id) throws Exception {
        proveedorServicio.desactivar(id);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Proveedor desactivado"));
    }

    @GetMapping("/proveedores")
    @Operation(summary = "Listar proveedores activos")
    public ResponseEntity<MensajeDTO<List<ProveedorDTO>>> listarProveedores() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, proveedorServicio.listarActivos()));
    }

    @GetMapping("/proveedores/{id}")
    @Operation(summary = "Detalle de proveedor")
    public ResponseEntity<MensajeDTO<ProveedorDTO>> obtenerProveedor(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, proveedorServicio.obtenerPorId(id)));
    }

    @GetMapping("/proveedores/buscar")
    @Operation(summary = "Buscar proveedor por nombre")
    public ResponseEntity<MensajeDTO<List<ProveedorDTO>>> buscarProveedores(
            @RequestParam String nombre) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, proveedorServicio.buscarPorNombre(nombre)));
    }

    @PatchMapping("/proveedores/{id}/calificacion")
    @Operation(summary = "Actualizar calificación del proveedor (0-5)")
    public ResponseEntity<MensajeDTO<ProveedorDTO>> calificarProveedor(
            @PathVariable Long id,
            @RequestParam Double calificacion) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        proveedorServicio.actualizarCalificacion(id, calificacion)));
    }

    // ── PRODUCTOS ─────────────────────────────────────────

    @PostMapping("/productos")
    @Operation(summary = "Crear producto")
    public ResponseEntity<MensajeDTO<ProductoDTO>> crearProducto(
            @Valid @RequestBody CrearProductoDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, productoServicio.crear(dto)));
    }

    @PutMapping("/productos/{id}")
    @Operation(summary = "Editar producto")
    public ResponseEntity<MensajeDTO<ProductoDTO>> editarProducto(
            @PathVariable Long id,
            @Valid @RequestBody CrearProductoDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, productoServicio.editar(id, dto)));
    }

    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Desactivar producto")
    public ResponseEntity<MensajeDTO<String>> desactivarProducto(
            @PathVariable Long id) throws Exception {
        productoServicio.desactivar(id);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Producto desactivado"));
    }

    @GetMapping("/productos")
    @Operation(summary = "Listar productos activos")
    public ResponseEntity<MensajeDTO<List<ProductoDTO>>> listarProductos() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, productoServicio.listarActivos()));
    }

    @GetMapping("/productos/{id}")
    @Operation(summary = "Detalle de producto")
    public ResponseEntity<MensajeDTO<ProductoDTO>> obtenerProducto(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, productoServicio.obtenerPorId(id)));
    }

    @GetMapping("/productos/codigo/{codigo}")
    @Operation(summary = "Buscar producto por código")
    public ResponseEntity<MensajeDTO<ProductoDTO>> obtenerPorCodigo(
            @PathVariable String codigo) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, productoServicio.obtenerPorCodigo(codigo)));
    }

    @GetMapping("/productos/buscar")
    @Operation(summary = "Buscar productos por nombre (paginado)")
    public ResponseEntity<MensajeDTO<Page<ProductoDTO>>> buscarProductos(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        productoServicio.buscarPorNombre(nombre, PageRequest.of(page, size))));
    }

    @GetMapping("/productos/categoria/{categoriaId}")
    @Operation(summary = "Productos por categoría")
    public ResponseEntity<MensajeDTO<List<ProductoDTO>>> listarPorCategoria(
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, productoServicio.listarPorCategoria(categoriaId)));
    }
}