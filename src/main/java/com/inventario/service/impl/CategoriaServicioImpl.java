package com.inventario.service.impl;

import com.inventario.dto.request.CrearCategoriaDTO;
import com.inventario.model.Categoria;
import com.inventario.repository.CategoriaRepository;
import com.inventario.service.interfaces.CategoriaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaServicioImpl implements CategoriaServicio {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria crear(CrearCategoriaDTO dto) throws Exception {
        if (categoriaRepository.existsByNombre(dto.nombre())) {
            throw new Exception("Ya existe una categoría con el nombre: " + dto.nombre());
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        categoria.setDescripcion(dto.descripcion());
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria editar(Long id, CrearCategoriaDTO dto) throws Exception {
        Categoria categoria = obtenerEntidad(id);
        if (!categoria.getNombre().equals(dto.nombre()) &&
                categoriaRepository.existsByNombre(dto.nombre())) {
            throw new Exception("Ya existe una categoría con el nombre: " + dto.nombre());
        }
        categoria.setNombre(dto.nombre());
        categoria.setDescripcion(dto.descripcion());
        return categoriaRepository.save(categoria);
    }

    @Override
    public void desactivar(Long id) throws Exception {
        Categoria categoria = obtenerEntidad(id);
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerPorId(Long id) throws Exception {
        return obtenerEntidad(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarActivas() {
        return categoriaRepository.findByActivaTrue();
    }

    // ── Método de apoyo ──────────────────────────────────
    public Categoria obtenerEntidad(Long id) throws Exception {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada con id: " + id));
    }
}