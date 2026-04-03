package com.inventario.dto.response;

import com.inventario.model.enums.EstadoAlerta;
import com.inventario.model.enums.TipoAlerta;

import java.time.LocalDateTime;

public record AlertaDTO(
        Long id,
        TipoAlerta tipo,
        EstadoAlerta estado,
        String productoNombre,
        String sucursalNombre,
        String mensaje,
        LocalDateTime fechaGeneracion,
        LocalDateTime fechaLectura
) {}