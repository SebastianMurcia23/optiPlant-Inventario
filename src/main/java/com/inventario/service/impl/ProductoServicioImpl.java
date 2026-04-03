package com.inventario.service.impl;

import com.inventario.dto.request.CrearProductoDTO;
import com.inventario.dto.response.ProductoDTO;
import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.UnidadMedida;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.UnidadMedidaRepository;
import com.inventario.service.interfaces.ProductoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductoServicioImpl implements ProductoServicio {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    @Override
    public ProductoDTO crear(CrearProductoDTO dto) throws Exception {
        if (productoRepository.existsByCodigo(dto.codigo())) {
            throw new Exception("Ya existe un producto con el código: " + dto.codigo());
        }

        Producto producto = new Producto();
        mapearDesdDTO(dto, producto);
        return mapearDTO(productoRepository.save(producto));
    }

    @Override
    public ProductoDTO editar(Long id, CrearProductoDTO dto) throws Exception {
        Producto producto = obtenerEntidad(id);

        if (!producto.getCodigo().equals(dto.codigo()) &&
                productoRepository.existsByCodigo(dto.codigo())) {
            throw new Exception("Ya existe un producto con el código: " + dto.codigo());
        }

        mapearDesdDTO(dto, producto);
        return mapearDTO(productoRepository.save(producto));
    }

    @Override
    public void desactivar(Long id) throws Exception {
        Producto producto = obtenerEntidad(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorCodigo(String codigo) throws Exception {
        return mapearDTO(productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new Exception("Producto no encontrado con código: " + codigo)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrue()
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return productoRepository
                .findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable)
                .map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(Long categoriaId) {
        return productoRepository.findActivosByCategoria(categoriaId)
                .stream().map(this::mapearDTO).toList();
    }

    // ── Métodos de apoyo ──────────────────────────────────
    public Producto obtenerEntidad(Long id) throws Exception {
        return productoRepository.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado con id: " + id));
    }

    private void mapearDesdDTO(CrearProductoDTO dto, Producto producto) throws Exception {
        producto.setCodigo(dto.codigo());
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecioVentaReferencia(dto.precioVentaReferencia());
        producto.setStockMinimoGlobal(
                dto.stockMinimoGlobal() != null ? dto.stockMinimoGlobal() : 0);
        producto.setTieneFechaVencimiento(dto.tieneFechaVencimiento());
        producto.setDiasAlertaVencimiento(
                dto.diasAlertaVencimiento() != null ? dto.diasAlertaVencimiento() : 30);

        UnidadMedida unidad = unidadMedidaRepository.findById(dto.unidadMedidaId())
                .orElseThrow(() -> new Exception(
                        "Unidad de medida no encontrada: " + dto.unidadMedidaId()));
        producto.setUnidadMedida(unidad);

        if (dto.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new Exception(
                            "Categoría no encontrada: " + dto.categoriaId()));
            producto.setCategoria(categoria);
        }
    }

    private ProductoDTO mapearDTO(Producto p) {
        return new ProductoDTO(
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getCategoria() != null ? p.getCategoria().getNombre() : null,
                p.getUnidadMedida().getNombre(),
                p.getUnidadMedida().getAbreviatura(),
                p.getPrecioVentaReferencia(),
                p.getCostoPromedio(),
                p.getStockMinimoGlobal(),
                p.isTieneFechaVencimiento(),
                p.getDiasAlertaVencimiento(),
                p.isActivo()
        );
    }
}