package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearProveedorDTO;
import com.inventario.dto.response.ProveedorDTO;

import java.util.List;

public interface ProveedorServicio {
    ProveedorDTO crear(CrearProveedorDTO dto) throws Exception;
    ProveedorDTO editar(Long id, CrearProveedorDTO dto) throws Exception;
    void desactivar(Long id) throws Exception;
    ProveedorDTO obtenerPorId(Long id) throws Exception;
    List<ProveedorDTO> listarActivos();
    List<ProveedorDTO> buscarPorNombre(String nombre);
    ProveedorDTO actualizarCalificacion(Long id, Double calificacion) throws Exception;
}