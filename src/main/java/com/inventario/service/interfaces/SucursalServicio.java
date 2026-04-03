package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearSucursalDTO;
import com.inventario.dto.response.SucursalDTO;

import java.util.List;

public interface SucursalServicio {
    SucursalDTO crear(CrearSucursalDTO dto) throws Exception;
    SucursalDTO editar(Long id, CrearSucursalDTO dto) throws Exception;
    void desactivar(Long id) throws Exception;
    SucursalDTO obtenerPorId(Long id) throws Exception;
    List<SucursalDTO> listarActivas();
    List<SucursalDTO> listarTodas();
}