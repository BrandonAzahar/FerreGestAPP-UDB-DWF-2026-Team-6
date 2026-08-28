IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ferregest_db')
BEGIN
    CREATE DATABASE ferregest_db;
END
GO

-- 2. Usar la base de datos creada
USE ferregest_db;
GO

-- 3. Crear tabla de categorías
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'categorias')
BEGIN
    CREATE TABLE categorias (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL,
        descripcion VARCHAR(MAX)
    );
END
GO

-- 4. Crear tabla de productos
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'productos')
BEGIN
    CREATE TABLE productos (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(150) NOT NULL,
        descripcion VARCHAR(MAX),
        precio DECIMAL(10,2) NOT NULL,
        stock INT NOT NULL DEFAULT 0,
        categoria_id INT,
        CONSTRAINT fk_productos_categorias
            FOREIGN KEY (categoria_id)
            REFERENCES categorias(id)
            ON DELETE SET NULL
    );
END
GO

-- 5. Insertar 5 categorías
INSERT INTO categorias (nombre, descripcion) VALUES
('Herramientas Manuales', 'Martillos, destornilladores, llaves, pinzas y similares'),
('Materiales de Construcción', 'Cemento, arena, grava, varillas de acero, etc.'),
('Electricidad', 'Cables, enchufes, interruptores, focos'),
('Plomería', 'Tubos, llaves, pegamentos, válvulas'),
('Pinturas', 'Pinturas de agua, aceite, brochas, rodillos');
GO

-- 6. Insertar 5 productos
INSERT INTO productos (nombre, descripcion, precio, stock, categoria_id) VALUES
('Martillo de Carpintero 16oz', 'Martillo con mango de madera resistente', 15.50, 50, 1),
('Destornillador Phillips', 'Destornillador cruz de acero inoxidable, punta magnética', 5.20, 100, 1),
('Saco de Cemento 50kg', 'Cemento Portland gris para construcción general', 8.50, 200, 2),
('Cable Calibre 12 THW', 'Cable eléctrico rojo de 100 metros', 45.00, 30, 3),
('Tubo PVC 1/2 pulgada', 'Tubo PVC para agua potable', 3.20, 150, 4);
GO

-- 7. Consultar categorías
SELECT * FROM categorias;

-- 8. Consultar productos
SELECT * FROM productos;

-- 9. Consultar productos con su categoría
SELECT
    p.id,
    p.nombre AS producto,
    p.descripcion,
    p.precio,
    p.stock,
    c.nombre AS categoria
FROM productos p
LEFT JOIN categorias c
    ON p.categoria_id = c.id;
GO
