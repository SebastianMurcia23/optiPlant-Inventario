package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearCategoriaDTO;
import com.inventario.model.Categoria;

import java.util.List;

public interface CategoriaServicio {
    Categoria crear(CrearCategoriaDTO dto) throws Exception;
    Categoria editar(Long id, CrearCategoriaDTO dto) throws Exception;
    void desactivar(Long id) throws Exception;
    Categoria obtenerPorId(Long id) throws Exception;
    List<Categoria> listarActivas();
}