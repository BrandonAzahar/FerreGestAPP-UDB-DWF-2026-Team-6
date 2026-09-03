SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS ferregest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ferregest;

CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria_id INT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL
);

INSERT INTO categorias (nombre, descripcion) VALUES 
('Herramientas Manuales', 'Martillos, destornilladores, llaves, pinzas y similares'),
('Materiales de Construcción', 'Cemento, arena, grava, varillas de acero, etc.'),
('Electricidad', 'Cables, enchufes, interruptores, focos'),
('Plomería', 'Tubos, llaves, pegamentos, válvulas'),
('Pinturas', 'Pinturas de agua, aceite, brochas, rodillos');

INSERT INTO productos (nombre, descripcion, precio, stock, categoria_id) VALUES 
('Martillo de Carpintero 16oz', 'Martillo con mango de madera resistente', 15.50, 50, 1),
('Destornillador Phillips', 'Destornillador cruz de acero inoxidable, punta magnética', 5.20, 100, 1),
('Saco de Cemento 50kg', 'Cemento Portland gris para construcción general', 8.50, 200, 2),
('Cable Calibre 12 THW', 'Cable eléctrico rojo de 100 metros', 45.00, 30, 3),
('Tubo PVC 1/2 pulgada', 'Tubo PVC para agua potable', 3.20, 150, 4);
