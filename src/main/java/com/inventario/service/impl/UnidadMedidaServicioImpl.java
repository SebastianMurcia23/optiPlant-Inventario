package com.inventario.service.impl;

import com.inventario.dto.request.CrearUnidadMedidaDTO;
import com.inventario.model.UnidadMedida;
import com.inventario.repository.UnidadMedidaRepository;
import com.inventario.service.interfaces.UnidadMedidaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UnidadMedidaServicioImpl implements UnidadMedidaServicio {

    private final UnidadMedidaRepository unidadMedidaRepository;

    @Override
    public UnidadMedida crear(CrearUnidadMedidaDTO dto) throws Exception {
        if (unidadMedidaRepository.existsByNombre(dto.nombre())) {
            throw new Exception("Ya existe una unidad con el nombre: " + dto.nombre());
        }
        if (unidadMedidaRepository.existsByAbreviatura(dto.abreviatura())) {
            throw new Exception("Ya existe una unidad con la abreviatura: " + dto.abreviatura());
        }
        UnidadMedida unidad = new UnidadMedida();
        unidad.setNombre(dto.nombre());
        unidad.setAbreviatura(dto.abreviatura());
        return unidadMedidaRepository.save(unidad);
    }

    @Override
    public UnidadMedida editar(Long id, CrearUnidadMedidaDTO dto) throws Exception {
        UnidadMedida unidad = obtenerEntidad(id);

        if (!unidad.getNombre().equals(dto.nombre()) &&
                unidadMedidaRepository.existsByNombre(dto.nombre())) {
            throw new Exception("Ya existe una unidad con el nombre: " + dto.nombre());
        }
        if (!unidad.getAbreviatura().equals(dto.abreviatura()) &&
                unidadMedidaRepository.existsByAbreviatura(dto.abreviatura())) {
            throw new Exception("Ya existe una unidad con la abreviatura: " + dto.abreviatura());
        }

        unidad.setNombre(dto.nombre());
        unidad.setAbreviatura(dto.abreviatura());
        return unidadMedidaRepository.save(unidad);
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedida obtenerPorId(Long id) throws Exception {
        return obtenerEntidad(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedida> listarTodas() {
        return unidadMedidaRepository.findAll();
    }

    // ── Método de apoyo ──────────────────────────────────
    public UnidadMedida obtenerEntidad(Long id) throws Exception {
        return unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new Exception(
                        "Unidad de medida no encontrada con id: " + id));
    }
}