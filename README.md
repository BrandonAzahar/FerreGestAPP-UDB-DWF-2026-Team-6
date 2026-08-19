# FerreGest - Sistema Web de Gestión e Inventario

FerreGest es un proyecto web enfocado en la gestión comercial y de inventario para una ferretería. Está construido utilizando el patrón **MVC puro** con tecnologías Java Web, priorizando el control total sobre la arquitectura sin depender de frameworks de alto nivel.

## Arquitectura y Tecnologías
- **Patrón**: Modelo-Vista-Controlador (MVC).
- **Lenguaje**: Java 17+
- **Controlador**: Java Servlet API (`javax.servlet`)
- **Vista**: JSP, JSTL, Expression Language, Bootstrap 5. *Regla estricta: Cero scriptlets (`<% %>`)*.
- **Modelo/Persistencia**: POJOs y patrón DAO interactuando directamente con **JDBC puro** y `PreparedStatement` (cero frameworks tipo Hibernate).
- **Base de Datos**: MySQL.
- **Gestión de dependencias**: Maven (empaquetado WAR).

## Instrucciones de Instalación y Ejecución

### 1. Preparar la Base de Datos
1. Asegúrate de tener **MySQL Server** en ejecución.
2. Abre tu cliente SQL favorito (MySQL Workbench, DBeaver, línea de comandos).
3. Ejecuta el script de creación ubicado en `database/ferregest.sql`.
   - Este script creará la base de datos `ferregest_db`.
   - Creará las tablas `categorias` y `productos` con sus respectivas relaciones.
   - Insertará 5 datos de prueba iniciales por cada tabla para validar el funcionamiento.

### 2. Configurar la Conexión JDBC
1. Abre el archivo `src/main/resources/db.properties`.
2. Revisa que `db.url` apunte al puerto correcto (usualmente 3306).
3. Modifica `db.user` y `db.password` colocando las credenciales válidas de tu instalación local de MySQL.

### 3. Compilar el Proyecto (Maven)
Desde tu IDE (VS Code, IntelliJ, Eclipse) usando las herramientas de Maven integradas, o desde la terminal ejecutando:
```bash
mvn clean package
```
Esto descargará las dependencias y empaquetará el proyecto en `target/ferregest.war`.

### 4. Desplegar en Apache Tomcat
1. Utiliza **Apache Tomcat 8.5 o 9.0** (compatible con el namespace `javax.*` integrado en XAMPP).
2. Copia el archivo `ferregest.war` generado en la carpeta `target/` y pégalo en la carpeta `webapps/` de tu instalación de Tomcat (`D:\xampp\tomcat\webapps\`).
3. Inicia el servidor Tomcat desde el panel de XAMPP.
4. Abre tu navegador y accede a los siguientes módulos:
   - **Productos**: `http://localhost:8080/ferregest/productos`
   - **Categorías**: `http://localhost:8080/ferregest/categorias`

*Nota: La aplicación contiene operaciones CRUD completas (Crear, Leer, Actualizar, Eliminar) para ambos módulos con validaciones de cliente utilizando HTML5 y JavaScript puro.*
