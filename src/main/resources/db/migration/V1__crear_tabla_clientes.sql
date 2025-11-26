-- V1__Create_cliente_table.sql
CREATE TABLE clientes(
    id_cliente BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL UNIQUE
)