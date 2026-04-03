package com.inventario.service.impl;

import com.inventario.dto.request.CrearSucursalDTO;
import com.inventario.dto.response.SucursalDTO;
import com.inventario.model.Sucursal;
import com.inventario.repository.SucursalRepository;
import com.inventario.service.interfaces.SucursalServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SucursalServicioImpl implements SucursalServicio {

    private final SucursalRepository sucursalRepository;

    @Override
    public SucursalDTO crear(CrearSucursalDTO dto) throws Exception {
        if (sucursalRepository.existsByNombre(dto.nombre())) {
            throw new Exception("Ya existe una sucursal con el nombre: " + dto.nombre());
        }
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.nombre());
        sucursal.setDireccion(dto.direccion());
        sucursal.setTelefono(dto.telefono());
        sucursal.setCiudad(dto.ciudad());
        sucursal.setPais(dto.pais());
        return mapearDTO(sucursalRepository.save(sucursal));
    }

    @Override
    public SucursalDTO editar(Long id, CrearSucursalDTO dto) throws Exception {
        Sucursal sucursal = obtenerEntidad(id);
        if (!sucursal.getNombre().equals(dto.nombre()) &&
                sucursalRepository.existsByNombre(dto.nombre())) {
            throw new Exception("Ya existe una sucursal con el nombre: " + dto.nombre());
        }
        sucursal.setNombre(dto.nombre());
        sucursal.setDireccion(dto.direccion());
        sucursal.setTelefono(dto.telefono());
        sucursal.setCiudad(dto.ciudad());
        sucursal.setPais(dto.pais());
        return mapearDTO(sucursalRepository.save(sucursal));
    }

    @Override
    public void desactivar(Long id) throws Exception {
        Sucursal sucursal = obtenerEntidad(id);
        sucursal.setActiva(false);
        sucursalRepository.save(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO> listarActivas() {
        return sucursalRepository.findByActivaTrue()
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO> listarTodas() {
        return sucursalRepository.findAll()
                .stream().map(this::mapearDTO).toList();
    }

    // ── Métodos de apoyo ──────────────────────────────────
    public Sucursal obtenerEntidad(Long id) throws Exception {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + id));
    }

    private SucursalDTO mapearDTO(Sucursal s) {
        return new SucursalDTO(s.getId(), s.getNombre(), s.getDireccion(),
                s.getTelefono(), s.getCiudad(), s.getPais(),
                s.isActiva(), s.getFechaCreacion());
    }
}