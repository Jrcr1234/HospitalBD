
-- Paso 1: Crear la base de datos
CREATE DATABASE hospital_db;

-- Paso 2: Seleccionar esa base de datos para usarla
USE hospital_db;

-- Paso 3: Crear la tabla de Usuarios
CREATE TABLE Usuario (
    id VARCHAR(50) PRIMARY KEY,
    clave VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

-- Paso 4: Crear la tabla de Pacientes
CREATE TABLE Paciente (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fechaNacimiento DATE,
    telefono VARCHAR(20)
);

-- Creación de la tabla Medicamento
CREATE TABLE Medicamento (
    codigo VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    presentacion VARCHAR(255)
);

-- Creación de la tabla MedicoDetalle
CREATE TABLE MedicoDetalle (
    id_medico VARCHAR(50) PRIMARY KEY, -- Mismo ID que en la tabla Usuario
    especialidad VARCHAR(100) NOT NULL, -- Columna para la especialidad
    -- Clave Foránea: asegura que cada id_medico exista en la tabla Usuario
    FOREIGN KEY (id_medico) REFERENCES Usuario(id)
        ON DELETE CASCADE -- Si se borra el Usuario, se borra el detalle del médico
        ON UPDATE CASCADE -- Si cambia el ID del Usuario, se actualiza aquí
);

-- =================================================================
-- PASO 5: AÑADIR DATOS INICIALES (AQUÍ VAN LOS INSERTS)
-- =================================================================
INSERT INTO Usuario (id, clave, nombre, tipo) VALUES ('admin', 'admin', 'Administrador del Sistema', 'Administrador');

-- Aquí se podría añadir más usuarios o pacientes
-- INSERT INTO Usuario (id, clave, nombre, tipo) VALUES ('med1', 'med1', 'Dr. House', 'Medico');
-- INSERT INTO Paciente (id, nombre, fechaNacimiento, telefono) VALUES ('123', 'Juan Perez', '1990-05-15', '88887777');