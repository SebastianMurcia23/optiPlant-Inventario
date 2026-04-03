package com.inventario.service.interfaces;

import com.inventario.dto.request.LoginDTO;
import com.inventario.dto.response.TokenDTO;

public interface AuthServicio {
    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;
}