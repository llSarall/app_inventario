-- =============================================
-- Sistema de Inventario de Almacén
-- Autor: Alejandro Mejía Herrera
-- Fecha: 05/11/2026
-- Script 02: datos iniciales (catálogos, usuarios de prueba y productos)
-- =============================================
USE inventario;

INSERT INTO roles (idRol, nombre) VALUES
    (1, 'ADMINISTRADOR'),
    (2, 'ALMACENISTA');

INSERT INTO tipos_movimiento (idTipoMovimiento, nombre) VALUES
    (1, 'ENTRADA'),
    (2, 'SALIDA');

-- Usuarios de prueba.
-- Las contraseñas se insertan en texto plano SOLO para facilitar la
-- instalación: al arrancar, la aplicación detecta las que no están
-- cifradas y las reemplaza por su hash BCrypt (ver DatosIniciales.java).
--   juan.perez@gmail.com      / admin123   -> Administrador
--   pedro.martinez@gmail.com    / almacen123 -> Almacenista
INSERT INTO usuarios (nombre, correo, contrasena, idRol, estatus) VALUES
    ('Juan Perez', 'juan.perez@gmail.com',   'admin123',   1, 1),
    ('Pedro Martinez',  'pedro.martinez@gmail.com', 'almacen123', 2, 1);

-- Productos de ejemplo (todos inician con cantidad 0, como pide el escenario)
INSERT INTO productos (nombre, descripcion, precio, cantidad, estatus) VALUES
    ('LAPTOP',    'Laptop 15 pulgadas',       3000.00, 0, 1),
    ('MONITOR',   'Monitor 24 pulgadas',      2000.00, 0, 1),
    ('TECLADO',   'Teclado USB',               150.00, 0, 1),
    ('MOUSE',     'Mouse inalámbrico',         100.00, 0, 1);
