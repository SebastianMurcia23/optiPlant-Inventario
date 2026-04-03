package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearProductoDTO;
import com.inventario.dto.response.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoServicio {
    ProductoDTO crear(CrearProductoDTO dto) throws Exception;
    ProductoDTO editar(Long id, CrearProductoDTO dto) throws Exception;
    void desactivar(Long id) throws Exception;
    ProductoDTO obtenerPorId(Long id) throws Exception;
    ProductoDTO obtenerPorCodigo(String codigo) throws Exception;
    List<ProductoDTO> listarActivos();
    Page<ProductoDTO> buscarPorNombre(String nombre, Pageable pageable);
    List<ProductoDTO> listarPorCategoria(Long categoriaId);
}