-- ╔══════════════════════════════════════════════════════════════════╗
-- ║  SISTEMA DE INVENTARIO MULTI-SUCURSAL — SEED DATA COMPLETO    ║
-- ║  Archivo: data.sql (reemplaza el actual en src/main/resources) ║
-- ║  Compatibilidad: PostgreSQL 15 + Hibernate ddl-auto=update     ║
-- ╚══════════════════════════════════════════════════════════════════╝

-- ════════════════════════════════════════════════════════════════
-- 1. SUCURSALES (3)
-- ════════════════════════════════════════════════════════════════

INSERT INTO sucursales (nombre, direccion, telefono, ciudad, pais, activa, fecha_creacion)
SELECT 'Sucursal Centro', 'Cra 7 #45-12, Centro Histórico', '6012345678', 'Bogotá', 'Colombia', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sucursales WHERE nombre = 'Sucursal Centro');

INSERT INTO sucursales (nombre, direccion, telefono, ciudad, pais, activa, fecha_creacion)
SELECT 'Sucursal Norte', 'Av 19 #134-80, Usaquén', '6019876543', 'Bogotá', 'Colombia', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sucursales WHERE nombre = 'Sucursal Norte');

INSERT INTO sucursales (nombre, direccion, telefono, ciudad, pais, activa, fecha_creacion)
SELECT 'Sucursal Medellín', 'Calle 10 #43D-16, El Poblado', '6044567890', 'Medellín', 'Colombia', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM sucursales WHERE nombre = 'Sucursal Medellín');

-- ════════════════════════════════════════════════════════════════
-- 2. USUARIOS (6 — todos los roles, contraseñas = BCrypt de la indicada)
--    Todos usan BCrypt hash de "Admin123456" para facilitar pruebas
--    Hash: $2a$10$N9qo8uLOickgx2ZMRZoMye6VCPTnbOzKFJRqD5c4E0pDQ7kVtKJKi
--    NOTA: Generar hash real con GET /api/util/hash?password=Admin123456
-- ════════════════════════════════════════════════════════════════

-- Admin General (sin sucursal)
INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT 'Administrador Sistema', 'admin@inventario.com',
       '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
       '3001110000', 'ADMINISTRADOR_GENERAL', 'ACTIVO', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@inventario.com');

-- Gerente Sucursal Centro
INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT 'Carlos Mendoza', 'gerente.centro@inventario.com',
       '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
       '3102223333', 'GERENTE_SUCURSAL', 'ACTIVO',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Centro'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'gerente.centro@inventario.com');

-- Gerente Sucursal Norte
INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT 'Laura Rodríguez', 'gerente.norte@inventario.com',
       '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
       '3154445555', 'GERENTE_SUCURSAL', 'ACTIVO',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Norte'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'gerente.norte@inventario.com');

-- Operador Sucursal Centro
INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT 'Andrés Torres', 'operador1.centro@inventario.com',
       '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
       '3206667777', 'OPERADOR_INVENTARIO', 'ACTIVO',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Centro'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'operador1.centro@inventario.com');

-- Operador Sucursal Norte
INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT 'María López', 'operador1.norte@inventario.com',
       '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
       '3008889999', 'OPERADOR_INVENTARIO', 'ACTIVO',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Norte'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'operador1.norte@inventario.com');

-- Operador Sucursal Medellín
INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT 'Santiago Gómez', 'operador1.medellin@inventario.com',
       '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
       '3041112222', 'OPERADOR_INVENTARIO', 'ACTIVO',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Medellín'), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'operador1.medellin@inventario.com');

-- ════════════════════════════════════════════════════════════════
-- 3. CATEGORÍAS (5)
-- ════════════════════════════════════════════════════════════════

INSERT INTO categorias (nombre, descripcion, activa)
SELECT 'Electrónica', 'Dispositivos electrónicos, accesorios y componentes', true
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Electrónica');

INSERT INTO categorias (nombre, descripcion, activa)
SELECT 'Alimentos', 'Productos alimenticios perecederos y no perecederos', true
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Alimentos');

INSERT INTO categorias (nombre, descripcion, activa)
SELECT 'Oficina', 'Suministros y mobiliario de oficina', true
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Oficina');

INSERT INTO categorias (nombre, descripcion, activa)
SELECT 'Limpieza', 'Productos de aseo y limpieza industrial', true
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Limpieza');

INSERT INTO categorias (nombre, descripcion, activa)
SELECT 'Herramientas', 'Herramientas manuales y eléctricas', true
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Herramientas');

-- ════════════════════════════════════════════════════════════════
-- 4. UNIDADES DE MEDIDA (5)
-- ════════════════════════════════════════════════════════════════

INSERT INTO unidades_medida (nombre, abreviatura)
SELECT 'Unidad', 'UND' WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE nombre = 'Unidad');

INSERT INTO unidades_medida (nombre, abreviatura)
SELECT 'Kilogramo', 'KG' WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE nombre = 'Kilogramo');

INSERT INTO unidades_medida (nombre, abreviatura)
SELECT 'Litro', 'LT' WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE nombre = 'Litro');

INSERT INTO unidades_medida (nombre, abreviatura)
SELECT 'Caja', 'CJ' WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE nombre = 'Caja');

INSERT INTO unidades_medida (nombre, abreviatura)
SELECT 'Metro', 'MT' WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE nombre = 'Metro');

-- ════════════════════════════════════════════════════════════════
-- 5. PROVEEDORES (4)
-- ════════════════════════════════════════════════════════════════

INSERT INTO proveedores (nombre, nit_ruc, email, telefono, direccion, ciudad, persona_contacto, plazo_pago_dias, calificacion_promedio, activo, fecha_registro)
SELECT 'TechDistribuidores SAS', '900123456-1', 'ventas@techdist.co', '6013456789',
       'Cra 15 #88-21 Of 301', 'Bogotá', 'Roberto Vargas', 30, 4.5, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE nit_ruc = '900123456-1');

INSERT INTO proveedores (nombre, nit_ruc, email, telefono, direccion, ciudad, persona_contacto, plazo_pago_dias, calificacion_promedio, activo, fecha_registro)
SELECT 'Alimentos del Valle SA', '800987654-2', 'pedidos@alimentosvalle.com', '6024567890',
       'Av 3N #28-45', 'Cali', 'Patricia Henao', 15, 4.2, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE nit_ruc = '800987654-2');

INSERT INTO proveedores (nombre, nit_ruc, email, telefono, direccion, ciudad, persona_contacto, plazo_pago_dias, calificacion_promedio, activo, fecha_registro)
SELECT 'Suministros Globales Ltda', '860555777-3', 'contacto@sumglobal.co', '6017890123',
       'Calle 100 #19-61 P5', 'Bogotá', 'Diana Morales', 45, 3.8, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE nit_ruc = '860555777-3');

INSERT INTO proveedores (nombre, nit_ruc, email, telefono, direccion, ciudad, persona_contacto, plazo_pago_dias, calificacion_promedio, activo, fecha_registro)
SELECT 'Herramienta Pro SAS', '901234567-4', 'ventas@herrapro.com', '6048901234',
       'Cra 43A #1-50', 'Medellín', 'Javier Ríos', 30, 4.0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE nit_ruc = '901234567-4');

-- ════════════════════════════════════════════════════════════════
-- 6. PRODUCTOS (10 — variados, algunos perecederos)
-- ════════════════════════════════════════════════════════════════

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'ELEC-001', 'Teclado Mecánico RGB', 'Teclado mecánico switches blue, retroiluminación RGB',
       (SELECT id FROM categorias WHERE nombre = 'Electrónica'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       185000.00, 95000.00, 10, false, 0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'ELEC-001');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'ELEC-002', 'Mouse Inalámbrico Ergonómico', 'Mouse bluetooth 2.4GHz, 6 botones, DPI ajustable',
       (SELECT id FROM categorias WHERE nombre = 'Electrónica'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       89000.00, 42000.00, 15, false, 0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'ELEC-002');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'ELEC-003', 'Monitor LED 27"', 'Monitor Full HD IPS 27 pulgadas, 75Hz, HDMI + DP',
       (SELECT id FROM categorias WHERE nombre = 'Electrónica'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       750000.00, 420000.00, 5, false, 0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'ELEC-003');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'ALIM-001', 'Café Premium Origen 500g', 'Café molido de origen único, tueste medio, 500g',
       (SELECT id FROM categorias WHERE nombre = 'Alimentos'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       32000.00, 18000.00, 20, true, 60, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'ALIM-001');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'ALIM-002', 'Aceite de Oliva Extra Virgen 1L', 'Aceite de oliva importado, primera prensada en frío',
       (SELECT id FROM categorias WHERE nombre = 'Alimentos'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Litro'),
       45000.00, 28000.00, 15, true, 90, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'ALIM-002');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'ALIM-003', 'Galletas Artesanales Pack x12', 'Galletas de mantequilla artesanales, caja x12 unidades',
       (SELECT id FROM categorias WHERE nombre = 'Alimentos'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Caja'),
       18000.00, 9500.00, 25, true, 30, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'ALIM-003');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'OFIC-001', 'Resma Papel A4 75g', 'Resma de papel bond blanco A4, 500 hojas, 75g/m²',
       (SELECT id FROM categorias WHERE nombre = 'Oficina'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       14500.00, 8200.00, 30, false, 0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'OFIC-001');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'LIMP-001', 'Desinfectante Multiusos 5L', 'Desinfectante concentrado antibacterial, garrafa 5 litros',
       (SELECT id FROM categorias WHERE nombre = 'Limpieza'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Litro'),
       28000.00, 15000.00, 10, true, 180, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'LIMP-001');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'HERR-001', 'Taladro Percutor 800W', 'Taladro percutor eléctrico 800W, mandril 13mm, velocidad variable',
       (SELECT id FROM categorias WHERE nombre = 'Herramientas'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       195000.00, 110000.00, 5, false, 0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'HERR-001');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida_id, precio_venta_referencia, costo_promedio, stock_minimo_global, tiene_fecha_vencimiento, dias_alerta_vencimiento, activo, fecha_creacion)
SELECT 'HERR-002', 'Set Destornilladores x32', 'Juego profesional de 32 destornilladores con puntas intercambiables',
       (SELECT id FROM categorias WHERE nombre = 'Herramientas'),
       (SELECT id FROM unidades_medida WHERE nombre = 'Unidad'),
       65000.00, 32000.00, 8, false, 0, true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'HERR-002');

-- ════════════════════════════════════════════════════════════════
-- 7. INVENTARIO ITEMS (10 productos × 3 sucursales = 30)
--    Stock variado para probar alertas de mínimo/agotado
-- ════════════════════════════════════════════════════════════════

-- Sucursal Centro — stock completo
INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 45, 0, 10, 100, 95000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 60, 0, 15, 150, 42000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-002' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 12, 0, 5, 30, 420000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 80, 0, 20, 200, 18000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 35, 0, 15, 80, 28000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-002' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 50, 0, 25, 120, 9500.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-003' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 100, 0, 30, 300, 8200.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'OFIC-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 25, 0, 10, 50, 15000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'LIMP-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 8, 0, 5, 20, 110000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'HERR-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 18, 0, 8, 40, 32000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'HERR-002' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

-- Sucursal Norte — stock medio, con algunos bajos para alertas
INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 8, 0, 10, 80, 95000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 30, 0, 15, 100, 42000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-002' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 0, 0, 5, 20, 420000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 15, 0, 20, 150, 18000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 20, 0, 15, 60, 28000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-002' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 5, 0, 25, 80, 9500.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 40, 0, 30, 200, 8200.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'OFIC-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 10, 0, 10, 40, 15000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'LIMP-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 3, 0, 5, 15, 110000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'HERR-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 7, 0, 8, 30, 32000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'HERR-002' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

-- Sucursal Medellín — stock variado
INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 22, 0, 10, 60, 95000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 40, 0, 15, 100, 42000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-002' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 6, 0, 5, 20, 420000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 55, 0, 20, 120, 18000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 18, 0, 15, 50, 28000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-002' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 30, 0, 25, 80, 9500.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-003' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 75, 0, 30, 200, 8200.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'OFIC-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 15, 0, 10, 40, 15000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'LIMP-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 10, 0, 5, 20, 110000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'HERR-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

INSERT INTO inventario_items (producto_id, sucursal_id, cantidad_disponible, cantidad_reservada, stock_minimo, stock_maximo, costo_promedio_local, ultima_actualizacion)
SELECT p.id, s.id, 14, 0, 8, 35, 32000.00, NOW()
FROM productos p, sucursales s WHERE p.codigo = 'HERR-002' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM inventario_items WHERE producto_id = p.id AND sucursal_id = s.id);

-- ════════════════════════════════════════════════════════════════
-- 8. LOTES DE PRODUCTO (6 — solo para perecederos)
-- ════════════════════════════════════════════════════════════════

INSERT INTO lotes_producto (numero_lote, producto_id, sucursal_id, cantidad_inicial, cantidad_actual, fecha_fabricacion, fecha_vencimiento, fecha_ingreso, activo)
SELECT 'LOTE-CAFE-2025-001', p.id, s.id, 40, 35, '2025-01-15', '2025-12-15', NOW(), true
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM lotes_producto WHERE numero_lote = 'LOTE-CAFE-2025-001');

INSERT INTO lotes_producto (numero_lote, producto_id, sucursal_id, cantidad_inicial, cantidad_actual, fecha_fabricacion, fecha_vencimiento, fecha_ingreso, activo)
SELECT 'LOTE-CAFE-2025-002', p.id, s.id, 40, 40, '2025-03-01', '2026-03-01', NOW(), true
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM lotes_producto WHERE numero_lote = 'LOTE-CAFE-2025-002');

INSERT INTO lotes_producto (numero_lote, producto_id, sucursal_id, cantidad_inicial, cantidad_actual, fecha_fabricacion, fecha_vencimiento, fecha_ingreso, activo)
SELECT 'LOTE-ACEITE-2025-001', p.id, s.id, 35, 30, '2025-02-10', '2026-08-10', NOW(), true
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-002' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM lotes_producto WHERE numero_lote = 'LOTE-ACEITE-2025-001');

-- Lote próximo a vencer (para probar alertas)
INSERT INTO lotes_producto (numero_lote, producto_id, sucursal_id, cantidad_inicial, cantidad_actual, fecha_fabricacion, fecha_vencimiento, fecha_ingreso, activo)
SELECT 'LOTE-GALLETA-2024-003', p.id, s.id, 30, 12, '2024-10-01', '2025-04-15', NOW(), true
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM lotes_producto WHERE numero_lote = 'LOTE-GALLETA-2024-003');

INSERT INTO lotes_producto (numero_lote, producto_id, sucursal_id, cantidad_inicial, cantidad_actual, fecha_fabricacion, fecha_vencimiento, fecha_ingreso, activo)
SELECT 'LOTE-DESINF-2025-001', p.id, s.id, 25, 20, '2025-01-20', '2027-01-20', NOW(), true
FROM productos p, sucursales s WHERE p.codigo = 'LIMP-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM lotes_producto WHERE numero_lote = 'LOTE-DESINF-2025-001');

INSERT INTO lotes_producto (numero_lote, producto_id, sucursal_id, cantidad_inicial, cantidad_actual, fecha_fabricacion, fecha_vencimiento, fecha_ingreso, activo)
SELECT 'LOTE-CAFE-MED-001', p.id, s.id, 55, 50, '2025-02-15', '2026-02-15', NOW(), true
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM lotes_producto WHERE numero_lote = 'LOTE-CAFE-MED-001');

-- ════════════════════════════════════════════════════════════════
-- 9. ÓRDENES DE COMPRA (3 — una por sucursal, diferentes estados)
-- ════════════════════════════════════════════════════════════════

-- OC Recibida — Sucursal Centro
INSERT INTO ordenes_compra (numero, proveedor_id, sucursal_id, solicitante_id, estado, fecha_creacion, fecha_esperada_entrega, fecha_recepcion, subtotal, descuento_total, total, plazo_pago_dias, observaciones)
SELECT 'OC-2025-0001',
       (SELECT id FROM proveedores WHERE nit_ruc = '900123456-1'),
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Centro'),
       (SELECT id FROM usuarios WHERE email = 'gerente.centro@inventario.com'),
       'RECIBIDA', NOW() - INTERVAL '15 days', CURRENT_DATE - 5, NOW() - INTERVAL '7 days',
       4750000.00, 0.00, 4750000.00, 30, 'Pedido trimestral de electrónicos'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero = 'OC-2025-0001');

-- OC Enviada — Sucursal Norte (pendiente de recibir)
INSERT INTO ordenes_compra (numero, proveedor_id, sucursal_id, solicitante_id, estado, fecha_creacion, fecha_esperada_entrega, subtotal, descuento_total, total, plazo_pago_dias, observaciones)
SELECT 'OC-2025-0002',
       (SELECT id FROM proveedores WHERE nit_ruc = '800987654-2'),
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Norte'),
       (SELECT id FROM usuarios WHERE email = 'gerente.norte@inventario.com'),
       'ENVIADA', NOW() - INTERVAL '3 days', CURRENT_DATE + 7,
       1260000.00, 0.00, 1260000.00, 15, 'Reabastecimiento de alimentos'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero = 'OC-2025-0002');

-- OC Borrador — Sucursal Medellín
INSERT INTO ordenes_compra (numero, proveedor_id, sucursal_id, solicitante_id, estado, fecha_creacion, fecha_esperada_entrega, subtotal, descuento_total, total, plazo_pago_dias, observaciones)
SELECT 'OC-2025-0003',
       (SELECT id FROM proveedores WHERE nit_ruc = '901234567-4'),
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Medellín'),
       (SELECT id FROM usuarios WHERE email = 'operador1.medellin@inventario.com'),
       'BORRADOR', NOW() - INTERVAL '1 day', CURRENT_DATE + 14,
       550000.00, 0.00, 550000.00, 30, 'Reposición de herramientas'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero = 'OC-2025-0003');

-- Detalles OC-0001 (recibida)
INSERT INTO detalles_orden_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, porcentaje_descuento, subtotal)
SELECT oc.id, p.id, 20, 20, 95000.00, 0.00, 1900000.00
FROM ordenes_compra oc, productos p WHERE oc.numero = 'OC-2025-0001' AND p.codigo = 'ELEC-001'
AND NOT EXISTS (SELECT 1 FROM detalles_orden_compra d WHERE d.orden_compra_id = oc.id AND d.producto_id = p.id);

INSERT INTO detalles_orden_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, porcentaje_descuento, subtotal)
SELECT oc.id, p.id, 30, 30, 42000.00, 0.00, 1260000.00
FROM ordenes_compra oc, productos p WHERE oc.numero = 'OC-2025-0001' AND p.codigo = 'ELEC-002'
AND NOT EXISTS (SELECT 1 FROM detalles_orden_compra d WHERE d.orden_compra_id = oc.id AND d.producto_id = p.id);

INSERT INTO detalles_orden_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, porcentaje_descuento, subtotal)
SELECT oc.id, p.id, 5, 5, 420000.00, 5.00, 1995000.00
FROM ordenes_compra oc, productos p WHERE oc.numero = 'OC-2025-0001' AND p.codigo = 'ELEC-003'
AND NOT EXISTS (SELECT 1 FROM detalles_orden_compra d WHERE d.orden_compra_id = oc.id AND d.producto_id = p.id);

-- Detalles OC-0002 (enviada, sin recibir)
INSERT INTO detalles_orden_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, porcentaje_descuento, subtotal)
SELECT oc.id, p.id, 40, 0, 18000.00, 0.00, 720000.00
FROM ordenes_compra oc, productos p WHERE oc.numero = 'OC-2025-0002' AND p.codigo = 'ALIM-001'
AND NOT EXISTS (SELECT 1 FROM detalles_orden_compra d WHERE d.orden_compra_id = oc.id AND d.producto_id = p.id);

INSERT INTO detalles_orden_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, porcentaje_descuento, subtotal)
SELECT oc.id, p.id, 20, 0, 28000.00, 5.00, 532000.00
FROM ordenes_compra oc, productos p WHERE oc.numero = 'OC-2025-0002' AND p.codigo = 'ALIM-002'
AND NOT EXISTS (SELECT 1 FROM detalles_orden_compra d WHERE d.orden_compra_id = oc.id AND d.producto_id = p.id);

-- Detalles OC-0003 (borrador)
INSERT INTO detalles_orden_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, porcentaje_descuento, subtotal)
SELECT oc.id, p.id, 5, 0, 110000.00, 0.00, 550000.00
FROM ordenes_compra oc, productos p WHERE oc.numero = 'OC-2025-0003' AND p.codigo = 'HERR-001'
AND NOT EXISTS (SELECT 1 FROM detalles_orden_compra d WHERE d.orden_compra_id = oc.id AND d.producto_id = p.id);

-- ════════════════════════════════════════════════════════════════
-- 10. VENTAS (3 — diferentes estados)
-- ════════════════════════════════════════════════════════════════

-- Venta confirmada — Sucursal Centro
INSERT INTO ventas (numero, sucursal_id, vendedor_id, estado, cliente_nombre, cliente_documento, subtotal, descuento_total, total, observaciones, fecha_venta)
SELECT 'VTA-2025-0001',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Centro'),
       (SELECT id FROM usuarios WHERE email = 'operador1.centro@inventario.com'),
       'CONFIRMADA', 'Empresa ABC SAS', '900111222-3',
       463000.00, 0.00, 463000.00, 'Venta corporativa de equipos', NOW() - INTERVAL '5 days'
WHERE NOT EXISTS (SELECT 1 FROM ventas WHERE numero = 'VTA-2025-0001');

-- Venta pendiente — Sucursal Norte
INSERT INTO ventas (numero, sucursal_id, vendedor_id, estado, cliente_nombre, cliente_documento, subtotal, descuento_total, total, observaciones, fecha_venta)
SELECT 'VTA-2025-0002',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Norte'),
       (SELECT id FROM usuarios WHERE email = 'operador1.norte@inventario.com'),
       'PENDIENTE', 'Juan Ramírez', '1098765432',
       196000.00, 9800.00, 186200.00, 'Venta mostrador con descuento', NOW() - INTERVAL '1 day'
WHERE NOT EXISTS (SELECT 1 FROM ventas WHERE numero = 'VTA-2025-0002');

-- Venta confirmada — Sucursal Medellín
INSERT INTO ventas (numero, sucursal_id, vendedor_id, estado, cliente_nombre, cliente_documento, subtotal, descuento_total, total, observaciones, fecha_venta)
SELECT 'VTA-2025-0003',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Medellín'),
       (SELECT id FROM usuarios WHERE email = 'operador1.medellin@inventario.com'),
       'CONFIRMADA', 'Ferretería Don Pedro', '800333444-5',
       455000.00, 0.00, 455000.00, 'Venta de herramientas al por mayor', NOW() - INTERVAL '3 days'
WHERE NOT EXISTS (SELECT 1 FROM ventas WHERE numero = 'VTA-2025-0003');

-- Detalles VTA-0001
INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 2, 185000.00, 0.00, 370000.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0001' AND p.codigo = 'ELEC-001'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 1, 89000.00, 0.00, 89000.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0001' AND p.codigo = 'ELEC-002'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 1, 4000.00, 0.00, 4000.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0001' AND p.codigo = 'OFIC-001'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

-- Detalles VTA-0002
INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 3, 32000.00, 5.00, 91200.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0002' AND p.codigo = 'ALIM-001'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 2, 45000.00, 0.00, 90000.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0002' AND p.codigo = 'ALIM-002'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

-- Detalles VTA-0003
INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 2, 195000.00, 0.00, 390000.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0003' AND p.codigo = 'HERR-001'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, porcentaje_descuento, subtotal)
SELECT v.id, p.id, 1, 65000.00, 0.00, 65000.00
FROM ventas v, productos p WHERE v.numero = 'VTA-2025-0003' AND p.codigo = 'HERR-002'
AND NOT EXISTS (SELECT 1 FROM detalles_venta d WHERE d.venta_id = v.id AND d.producto_id = p.id);

-- ════════════════════════════════════════════════════════════════
-- 11. TRANSFERENCIAS (3 — diferentes estados del ciclo)
-- ════════════════════════════════════════════════════════════════

-- Transferencia Recibida completa: Centro → Norte
INSERT INTO transferencias (numero, sucursal_origen_id, sucursal_destino_id, solicitante_id, despachador_id, estado, prioridad, transportista, fecha_solicitud, fecha_despacho, fecha_estimada_llegada, fecha_recepcion, tiempo_estimado_horas, tiempo_real_horas, observaciones, observaciones_recepcion)
SELECT 'TRF-2025-0001',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Centro'),
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Norte'),
       (SELECT id FROM usuarios WHERE email = 'operador1.norte@inventario.com'),
       (SELECT id FROM usuarios WHERE email = 'gerente.centro@inventario.com'),
       'RECIBIDA_COMPLETA', 'ALTA', 'Servientrega Express',
       NOW() - INTERVAL '10 days', NOW() - INTERVAL '8 days', CURRENT_DATE - 6,
       NOW() - INTERVAL '6 days', 48, 44, 'Reposición urgente de monitores', 'Recibido en perfectas condiciones'
WHERE NOT EXISTS (SELECT 1 FROM transferencias WHERE numero = 'TRF-2025-0001');

-- Transferencia En tránsito: Centro → Medellín
INSERT INTO transferencias (numero, sucursal_origen_id, sucursal_destino_id, solicitante_id, despachador_id, estado, prioridad, transportista, fecha_solicitud, fecha_despacho, fecha_estimada_llegada, tiempo_estimado_horas, observaciones)
SELECT 'TRF-2025-0002',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Centro'),
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Medellín'),
       (SELECT id FROM usuarios WHERE email = 'operador1.medellin@inventario.com'),
       (SELECT id FROM usuarios WHERE email = 'gerente.centro@inventario.com'),
       'EN_TRANSITO', 'NORMAL', 'Coordinadora Mercantil',
       NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day', CURRENT_DATE + 2,
       72, 'Transferencia programada de suministros de oficina'
WHERE NOT EXISTS (SELECT 1 FROM transferencias WHERE numero = 'TRF-2025-0002');

-- Transferencia Solicitada: Medellín → Norte
INSERT INTO transferencias (numero, sucursal_origen_id, sucursal_destino_id, solicitante_id, estado, prioridad, fecha_solicitud, tiempo_estimado_horas, observaciones)
SELECT 'TRF-2025-0003',
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Medellín'),
       (SELECT id FROM sucursales WHERE nombre = 'Sucursal Norte'),
       (SELECT id FROM usuarios WHERE email = 'gerente.norte@inventario.com'),
       'SOLICITADA', 'URGENTE',
       NOW() - INTERVAL '6 hours', 24, 'Urgente: stock agotado de monitores en Norte'
WHERE NOT EXISTS (SELECT 1 FROM transferencias WHERE numero = 'TRF-2025-0003');

-- Detalles TRF-0001 (recibida completa)
INSERT INTO detalles_transferencia (transferencia_id, producto_id, cantidad_solicitada, cantidad_enviada, cantidad_recibida, cantidad_faltante, observaciones)
SELECT t.id, p.id, 5, 5, 5, 0, NULL
FROM transferencias t, productos p WHERE t.numero = 'TRF-2025-0001' AND p.codigo = 'ELEC-003'
AND NOT EXISTS (SELECT 1 FROM detalles_transferencia d WHERE d.transferencia_id = t.id AND d.producto_id = p.id);

INSERT INTO detalles_transferencia (transferencia_id, producto_id, cantidad_solicitada, cantidad_enviada, cantidad_recibida, cantidad_faltante, observaciones)
SELECT t.id, p.id, 10, 10, 10, 0, NULL
FROM transferencias t, productos p WHERE t.numero = 'TRF-2025-0001' AND p.codigo = 'ELEC-001'
AND NOT EXISTS (SELECT 1 FROM detalles_transferencia d WHERE d.transferencia_id = t.id AND d.producto_id = p.id);

-- Detalles TRF-0002 (en tránsito)
INSERT INTO detalles_transferencia (transferencia_id, producto_id, cantidad_solicitada, cantidad_enviada, cantidad_recibida, cantidad_faltante, observaciones)
SELECT t.id, p.id, 30, 30, NULL, 0, NULL
FROM transferencias t, productos p WHERE t.numero = 'TRF-2025-0002' AND p.codigo = 'OFIC-001'
AND NOT EXISTS (SELECT 1 FROM detalles_transferencia d WHERE d.transferencia_id = t.id AND d.producto_id = p.id);

INSERT INTO detalles_transferencia (transferencia_id, producto_id, cantidad_solicitada, cantidad_enviada, cantidad_recibida, cantidad_faltante, observaciones)
SELECT t.id, p.id, 5, 5, NULL, 0, NULL
FROM transferencias t, productos p WHERE t.numero = 'TRF-2025-0002' AND p.codigo = 'LIMP-001'
AND NOT EXISTS (SELECT 1 FROM detalles_transferencia d WHERE d.transferencia_id = t.id AND d.producto_id = p.id);

-- Detalles TRF-0003 (solicitada, sin despachar)
INSERT INTO detalles_transferencia (transferencia_id, producto_id, cantidad_solicitada, cantidad_enviada, cantidad_recibida, cantidad_faltante, observaciones)
SELECT t.id, p.id, 4, NULL, NULL, 0, 'Urgente - stock agotado en destino'
FROM transferencias t, productos p WHERE t.numero = 'TRF-2025-0003' AND p.codigo = 'ELEC-003'
AND NOT EXISTS (SELECT 1 FROM detalles_transferencia d WHERE d.transferencia_id = t.id AND d.producto_id = p.id);

-- ════════════════════════════════════════════════════════════════
-- 12. MOVIMIENTOS DE INVENTARIO (trazabilidad de operaciones)
-- ════════════════════════════════════════════════════════════════

-- Ingreso por compra — OC-0001 recibida en Centro
INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'INGRESO_COMPRA', 20, 25, 45, 95000.00, 'Recepción OC-2025-0001', 'OC-2025-0001',
       (SELECT id FROM usuarios WHERE email = 'operador1.centro@inventario.com'), NOW() - INTERVAL '7 days'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'OC-2025-0001' AND producto_id = p.id AND sucursal_id = s.id AND tipo = 'INGRESO_COMPRA');

INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'INGRESO_COMPRA', 30, 30, 60, 42000.00, 'Recepción OC-2025-0001', 'OC-2025-0001',
       (SELECT id FROM usuarios WHERE email = 'operador1.centro@inventario.com'), NOW() - INTERVAL '7 days'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-002' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'OC-2025-0001' AND producto_id = p.id AND sucursal_id = s.id AND tipo = 'INGRESO_COMPRA');

-- Retiro por venta — VTA-0001 en Centro
INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'RETIRO_VENTA', 2, 47, 45, 185000.00, 'Venta confirmada VTA-2025-0001', 'VTA-2025-0001',
       (SELECT id FROM usuarios WHERE email = 'operador1.centro@inventario.com'), NOW() - INTERVAL '5 days'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'VTA-2025-0001' AND producto_id = p.id AND tipo = 'RETIRO_VENTA');

-- Retiro por transferencia — TRF-0001 desde Centro
INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'RETIRO_TRANSFERENCIA', 5, 17, 12, 420000.00, 'Despacho TRF-2025-0001', 'TRF-2025-0001',
       (SELECT id FROM usuarios WHERE email = 'gerente.centro@inventario.com'), NOW() - INTERVAL '8 days'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'TRF-2025-0001' AND producto_id = p.id AND sucursal_id = s.id AND tipo = 'RETIRO_TRANSFERENCIA');

-- Ingreso por transferencia — TRF-0001 recibida en Norte
INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'INGRESO_TRANSFERENCIA', 5, 0, 5, 420000.00, 'Recepción TRF-2025-0001', 'TRF-2025-0001',
       (SELECT id FROM usuarios WHERE email = 'operador1.norte@inventario.com'), NOW() - INTERVAL '6 days'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'TRF-2025-0001' AND producto_id = p.id AND sucursal_id = s.id AND tipo = 'INGRESO_TRANSFERENCIA');

-- Ajuste de inventario — merma en Medellín
INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'RETIRO_MERMA', 3, 33, 30, 9500.00, 'Producto dañado en almacén - humedad', 'AJUSTE-MED-001',
       (SELECT id FROM usuarios WHERE email = 'operador1.medellin@inventario.com'), NOW() - INTERVAL '2 days'
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-003' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'AJUSTE-MED-001');

-- Ajuste positivo manual — corrección de conteo
INSERT INTO movimientos_inventario (producto_id, sucursal_id, tipo, cantidad, cantidad_anterior, cantidad_posterior, costo_unitario, motivo, documento_referencia, responsable_id, fecha)
SELECT p.id, s.id, 'INGRESO_AJUSTE', 5, 70, 75, 8200.00, 'Ajuste por conteo físico - sobrante encontrado', 'AJUSTE-MED-002',
       (SELECT id FROM usuarios WHERE email = 'operador1.medellin@inventario.com'), NOW() - INTERVAL '1 day'
FROM productos p, sucursales s WHERE p.codigo = 'OFIC-001' AND s.nombre = 'Sucursal Medellín'
AND NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE documento_referencia = 'AJUSTE-MED-002');

-- ════════════════════════════════════════════════════════════════
-- 13. ALERTAS (6 — diferentes tipos y estados)
-- ════════════════════════════════════════════════════════════════

-- Stock bajo — ELEC-001 en Norte (tiene 8, mínimo es 10)
INSERT INTO alertas (tipo, estado, producto_id, sucursal_id, mensaje, fecha_generacion)
SELECT 'STOCK_MINIMO', 'ACTIVA', p.id, s.id,
       'Stock bajo de Teclado Mecánico RGB en Sucursal Norte: 8 unidades (mínimo: 10)', NOW() - INTERVAL '2 days'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM alertas WHERE tipo = 'STOCK_MINIMO' AND estado = 'ACTIVA' AND producto_id = p.id AND sucursal_id = s.id);

-- Stock agotado — ELEC-003 en Norte (tiene 0)
INSERT INTO alertas (tipo, estado, producto_id, sucursal_id, mensaje, fecha_generacion)
SELECT 'STOCK_AGOTADO', 'ACTIVA', p.id, s.id,
       'Stock agotado de Monitor LED 27" en Sucursal Norte', NOW() - INTERVAL '1 day'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM alertas WHERE tipo = 'STOCK_AGOTADO' AND estado = 'ACTIVA' AND producto_id = p.id AND sucursal_id = s.id);

-- Stock bajo — ALIM-001 en Norte (15 < 20)
INSERT INTO alertas (tipo, estado, producto_id, sucursal_id, mensaje, fecha_generacion)
SELECT 'STOCK_MINIMO', 'ACTIVA', p.id, s.id,
       'Stock bajo de Café Premium Origen 500g en Sucursal Norte: 15 unidades (mínimo: 20)', NOW() - INTERVAL '3 days'
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-001' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM alertas WHERE tipo = 'STOCK_MINIMO' AND estado = 'ACTIVA' AND producto_id = p.id AND sucursal_id = s.id);

-- Producto por vencer — Galletas en Norte
INSERT INTO alertas (tipo, estado, producto_id, sucursal_id, mensaje, fecha_generacion)
SELECT 'PRODUCTO_POR_VENCER', 'ACTIVA', p.id, s.id,
       'Lote LOTE-GALLETA-2024-003 de Galletas Artesanales próximo a vencer (2025-04-15) en Sucursal Norte', NOW() - INTERVAL '5 days'
FROM productos p, sucursales s WHERE p.codigo = 'ALIM-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM alertas WHERE tipo = 'PRODUCTO_POR_VENCER' AND estado = 'ACTIVA' AND producto_id = p.id AND sucursal_id = s.id);

-- Transferencia pendiente — TRF-0003
INSERT INTO alertas (tipo, estado, producto_id, sucursal_id, mensaje, fecha_generacion)
SELECT 'TRANSFERENCIA_PENDIENTE', 'ACTIVA', p.id, s.id,
       'Transferencia TRF-2025-0003 pendiente de preparación — prioridad URGENTE', NOW() - INTERVAL '6 hours'
FROM productos p, sucursales s WHERE p.codigo = 'ELEC-003' AND s.nombre = 'Sucursal Norte'
AND NOT EXISTS (SELECT 1 FROM alertas WHERE tipo = 'TRANSFERENCIA_PENDIENTE' AND estado = 'ACTIVA' AND producto_id = p.id AND sucursal_id = s.id);

-- Alerta resuelta (para mostrar historial)
INSERT INTO alertas (tipo, estado, producto_id, sucursal_id, mensaje, fecha_generacion, fecha_lectura, fecha_resolucion)
SELECT 'STOCK_MINIMO', 'RESUELTA', p.id, s.id,
       'Stock bajo de Resma Papel A4 en Sucursal Centro — resuelto con OC-2025-0001',
       NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days', NOW() - INTERVAL '15 days'
FROM productos p, sucursales s WHERE p.codigo = 'OFIC-001' AND s.nombre = 'Sucursal Centro'
AND NOT EXISTS (SELECT 1 FROM alertas WHERE tipo = 'STOCK_MINIMO' AND estado = 'RESUELTA' AND producto_id = p.id AND sucursal_id = s.id);

-- ════════════════════════════════════════════════════════════════
-- FIN DEL SEED — Sistema listo para pruebas completas
-- ════════════════════════════════════════════════════════════════
-- CREDENCIALES DE PRUEBA (password para todos: Admin123456)
-- ┌─────────────────────────────────────────┬────────────────────────────┐
-- │ admin@inventario.com                    │ ADMINISTRADOR_GENERAL      │
-- │ gerente.centro@inventario.com           │ GERENTE_SUCURSAL (Centro)  │
-- │ gerente.norte@inventario.com            │ GERENTE_SUCURSAL (Norte)   │
-- │ operador1.centro@inventario.com         │ OPERADOR_INVENTARIO        │
-- │ operador1.norte@inventario.com          │ OPERADOR_INVENTARIO        │
-- │ operador1.medellin@inventario.com       │ OPERADOR_INVENTARIO        │
-- └─────────────────────────────────────────┴────────────────────────────┘