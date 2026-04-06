-- Este script se ejecuta después de que Hibernate crea las tablas
-- Solo inserta el admin si no existe

INSERT INTO usuarios (nombre, email, password, telefono, rol, estado, sucursal_id, fecha_registro)
SELECT
    'Administrador Sistema',
    'admin@inventario.com',
    '$2a$10$3WupHH3OYmSRwYkOw9oGKOmBKBHlgHeYFufqnrrnwHRaWqqn9fAeS',
    '3001110000',
    'ADMINISTRADOR_GENERAL',
    'ACTIVO',
    NULL,
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'admin@inventario.com'
);