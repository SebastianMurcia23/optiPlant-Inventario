package com.inventario.service.impl;

import com.inventario.dto.request.CrearProveedorDTO;
import com.inventario.dto.response.ProveedorDTO;
import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import com.inventario.service.interfaces.ProveedorServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorServicioImpl implements ProveedorServicio {

    private final ProveedorRepository proveedorRepository;

    @Override
    public ProveedorDTO crear(CrearProveedorDTO dto) throws Exception {
        if (dto.nitRuc() != null && proveedorRepository.existsByNitRuc(dto.nitRuc())) {
            throw new Exception("Ya existe un proveedor con NIT/RUC: " + dto.nitRuc());
        }
        Proveedor proveedor = new Proveedor();
        mapearDesdeDTO(dto, proveedor);
        return mapearDTO(proveedorRepository.save(proveedor));
    }

    @Override
    public ProveedorDTO editar(Long id, CrearProveedorDTO dto) throws Exception {
        Proveedor proveedor = obtenerEntidad(id);

        if (dto.nitRuc() != null &&
                !dto.nitRuc().equals(proveedor.getNitRuc()) &&
                proveedorRepository.existsByNitRuc(dto.nitRuc())) {
            throw new Exception("Ya existe un proveedor con NIT/RUC: " + dto.nitRuc());
        }

        mapearDesdeDTO(dto, proveedor);
        return mapearDTO(proveedorRepository.save(proveedor));
    }

    @Override
    public void desactivar(Long id) throws Exception {
        Proveedor proveedor = obtenerEntidad(id);
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarActivos() {
        return proveedorRepository.findByActivoTrue()
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    public ProveedorDTO actualizarCalificacion(Long id, Double calificacion) throws Exception {
        if (calificacion < 0 || calificacion > 5) {
            throw new Exception("La calificación debe estar entre 0 y 5");
        }
        Proveedor proveedor = obtenerEntidad(id);
        proveedor.setCalificacionPromedio(calificacion);
        return mapearDTO(proveedorRepository.save(proveedor));
    }

    // ── Métodos de apoyo ──────────────────────────────────
    public Proveedor obtenerEntidad(Long id) throws Exception {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new Exception("Proveedor no encontrado con id: " + id));
    }

    private void mapearDesdeDTO(CrearProveedorDTO dto, Proveedor proveedor) {
        proveedor.setNombre(dto.nombre());
        proveedor.setNitRuc(dto.nitRuc());
        proveedor.setEmail(dto.email());
        proveedor.setTelefono(dto.telefono());
        proveedor.setDireccion(dto.direccion());
        proveedor.setCiudad(dto.ciudad());
        proveedor.setPersonaContacto(dto.personaContacto());
        proveedor.setPlazoPagoDias(dto.plazoPagoDias());
    }

    private ProveedorDTO mapearDTO(Proveedor p) {
        return new ProveedorDTO(
                p.getId(), p.getNombre(), p.getNitRuc(),
                p.getEmail(), p.getTelefono(), p.getCiudad(),
                p.getPersonaContacto(), p.getPlazoPagoDias(),
                p.getCalificacionPromedio(), p.isActivo()
        );
    }
}