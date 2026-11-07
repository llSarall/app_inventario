-- =============================================
-- Sistema de Inventario de Almacén
-- Autor: Alejandro Mejía Herrera
-- Fecha: 05/11/2026
-- Script 01: Creación de la base de datos y sus tablas (MySQL 8)
-- =============================================

DROP DATABASE IF EXISTS inventario;
CREATE DATABASE inventario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventario;

-- Tabla de Roles (Administrador, Almacenista) sirve para registrar los distintos roles que vamos a manejar
CREATE TABLE roles (
    idRol   INT(2)       NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(30)  NOT NULL,
    PRIMARY KEY (idRol),
    UNIQUE KEY uk_roles_nombre (nombre)
) ENGINE = InnoDB;

-- Tabla de Usuarios sirve para registrar los usuarios que daremos de alta
CREATE TABLE usuarios (
    idUsuario   INT(6)        NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100)  NOT NULL,
    correo      VARCHAR(50)   NOT NULL,
    contrasena  VARCHAR(100)  NOT NULL,
    idRol       INT(2)        NOT NULL,
    estatus     INT(1)        NOT NULL DEFAULT 1,
    PRIMARY KEY (idUsuario),
    UNIQUE KEY uk_usuarios_correo (correo),
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (idRol) REFERENCES roles (idRol)
) ENGINE = InnoDB;

-- Tabla de Productos sirve para registrar los productos que daremos de alta, la cantidad empieza en 0 por defecto y el registro nace con un estatus 1 (activo)
CREATE TABLE productos (
    idProducto     INT(6)         NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)   NOT NULL,
    descripcion    VARCHAR(255)   NULL,
    precio         DECIMAL(16,2)  NOT NULL DEFAULT 0.00,
    cantidad       INT(6)         NOT NULL DEFAULT 0,
    estatus        INT(1)         NOT NULL DEFAULT 1,
    fechaRegistro  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idProducto),
    UNIQUE KEY uk_productos_nombre (nombre),
    CONSTRAINT chk_productos_cantidad CHECK (cantidad >= 0)
) ENGINE = InnoDB;

-- Tabla de Tipo de Movimientos (ENTRADA, SALIDA) sirve para registrar los distintos movimientos que vamos a manejar
CREATE TABLE tipos_movimiento (
    idTipoMovimiento  INT(2)       NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(20)  NOT NULL,
    PRIMARY KEY (idTipoMovimiento),
    UNIQUE KEY uk_tipos_movimiento_nombre (nombre)
) ENGINE = InnoDB;


-- Tabla de Histórico de Movimientos sirve para registrar los movimientos que se hacen dentro del sistema (qué producto, qué tipo de movimiento, cantidad, quién lo hizo y cuándo)
CREATE TABLE movimientos (
    idMovimiento      INT(10)   NOT NULL AUTO_INCREMENT,
    idProducto        INT(6)    NOT NULL,
    idTipoMovimiento  INT(2)    NOT NULL,
    idUsuario         INT(6)    NOT NULL,
    cantidad          INT(6)    NOT NULL,
    fechaHora         DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idMovimiento),
    KEY idx_movimientos_tipo (idTipoMovimiento),
    KEY idx_movimientos_fecha (fechaHora),
    CONSTRAINT fk_movimientos_productos FOREIGN KEY (idProducto)       REFERENCES productos (idProducto),
    CONSTRAINT fk_movimientos_tipos     FOREIGN KEY (idTipoMovimiento) REFERENCES tipos_movimiento (idTipoMovimiento),
    CONSTRAINT fk_movimientos_usuarios  FOREIGN KEY (idUsuario)        REFERENCES usuarios (idUsuario),
    CONSTRAINT chk_movimientos_cantidad CHECK (cantidad > 0)
) ENGINE = InnoDB;
