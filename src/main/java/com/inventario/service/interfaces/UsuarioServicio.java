package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearUsuarioDTO;
import com.inventario.dto.request.EditarUsuarioDTO;
import com.inventario.dto.response.UsuarioDTO;

import java.util.List;

public interface UsuarioServicio {
    UsuarioDTO crear(CrearUsuarioDTO dto) throws Exception;
    UsuarioDTO editar(EditarUsuarioDTO dto) throws Exception;
    void eliminar(Long id) throws Exception;
    UsuarioDTO obtenerPorId(Long id) throws Exception;
    List<UsuarioDTO> listarPorSucursal(Long sucursalId);
    List<UsuarioDTO> listarTodos();
}