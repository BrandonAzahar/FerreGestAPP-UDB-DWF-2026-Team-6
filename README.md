# FerreGest - Sistema Web de Gestión e Inventario

FerreGest es una plataforma integral para la gestión comercial y de inventario de una ferretería, evolucionada a lo largo de tres fases arquitectónicas:
- **Fase 1 (Sprint 1)**: Patrón MVC con Servlets, JSP/JSTL y JDBC.
- **Fase 2 (Sprint 2)**: Migración a **JavaServer Faces (JSF 2.3 + AJAX)** con persistencia **JPA 2.2 / Hibernate 5.6** y contenedor CDI (**Weld**).
- **Fase 3 (Sprint 3)**: Construcción de una **API REST independiente** con **JAX-RS (Jersey 2.35)** y **Jackson** para serialización JSON, reutilizando los DAOs con JPA de la Fase 2, junto con un **Cliente Externo desacoplado** construido con HTML5, Bootstrap 5 y **Vanilla JavaScript (Fetch API)**.

---

## Arquitectura y Tecnologías

| Capa | Tecnología |
| :--- | :--- |
| **Lenguaje y Build** | Java 17+, Apache Maven (empaquetado `WAR`) |
| **Persistencia (Fase 2 y 3)** | JPA 2.2 + Hibernate Core 5.6.15.Final (`CategoriaDAO`, `ProductoDAO`) |
| **Vista Servidor (Fase 2)** | JSF 2.3 (Mojarra) + Facelets (`.xhtml`) + CDI (Weld) |
| **API REST (Fase 3)** | JAX-RS 2.1 (Jersey 2.35) + Jackson (`@JsonIgnore`, `jersey-media-json-jackson`) |
| **Cliente Externo (Fase 3)** | HTML5 + Bootstrap 5.3 + Vanilla JavaScript (`fetch()`) en `/cliente/index.html` |
| **Base de Datos** | MySQL 8.x (`ferregest_db`) |
| **Servidor de Aplicaciones** | Apache Tomcat 8.5 / 9.0 (`javax.*`) |

---

## Documentación Técnica de la API REST (Fase 3)

La configuración base de JAX-RS se define en `src/main/java/com/ferregest/config/RestConfig.java` mediante `@ApplicationPath("/api")`. Todas las rutas consumen y producen `application/json` y retornan códigos de estado HTTP semánticos (`200`, `201`, `400`, `404`, `500`) mediante `javax.ws.rs.core.Response`.

Para evitar ciclos de recursión infinita durante la serialización JSON bidireccional, las entidades JPA incluyen `@JsonIgnore` en sus relaciones `@OneToMany` (`Categoria.productos`) y `@ManyToOne` (`Producto.categoria`).

### URL Base
```text
http://localhost:8080/ferregest/api
```

---

### 1. Recurso Categorías (`/api/categorias`)
Clase: `com.ferregest.rest.CategoriaResource`

| Método HTTP | Ruta | Descripción | Códigos de Estado HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categorias` | Lista todas las categorías registradas. | `200 OK`, `500 Internal Server Error` |
| `GET` | `/api/categorias/{id}` | Obtiene el detalle de una categoría por su ID. | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |
| `GET` | `/api/categorias/{id}/productos` | Lista los productos asociados a una categoría. | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |
| `POST` | `/api/categorias` | Crea una nueva categoría (valida que `nombre` no esté vacío ni duplicado). | `201 Created`, `400 Bad Request`, `500 Internal Server Error` |
| `PUT` | `/api/categorias/{id}` | Actualiza una categoría existente por su ID. | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |
| `DELETE` | `/api/categorias/{id}` | Elimina una categoría desvinculando sus productos asociados (`SET NULL`). | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |

#### Ejemplo de Cuerpo JSON (`POST` / `PUT /api/categorias`)
```json
{
  "nombre": "Seguridad Industrial",
  "descripcion": "Cascos, guantes, botas dieléctricas y gafas de protección"
}
```

---

### 2. Recurso Productos (`/api/productos`)
Clase: `com.ferregest.rest.ProductoResource`

| Método HTTP | Ruta | Descripción | Códigos de Estado HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/productos` | Lista todos los productos (soporta filtros opcionales `?categoriaId=` y `?buscar=`). | `200 OK`, `500 Internal Server Error` |
| `GET` | `/api/productos/{id}` | Obtiene un producto específico por su ID. | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |
| `POST` | `/api/productos` | Crea un nuevo producto (valida `nombre` no vacío, `precio > 0`, `stock >= 0`). | `201 Created`, `400 Bad Request`, `500 Internal Server Error` |
| `PUT` | `/api/productos/{id}` | Actualiza un producto existente por su ID. | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |
| `DELETE` | `/api/productos/{id}` | Elimina un producto del catálogo por su ID. | `200 OK`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error` |

#### Ejemplo de Cuerpo JSON (`POST` / `PUT /api/productos`)
```json
{
  "nombre": "Esmeriladora Angular 4.5 Pulgadas 850W",
  "descripcion": "Esmeriladora compacta de 11000 RPM para corte y desbaste",
  "precio": 54.99,
  "stock": 18,
  "categoriaId": 2
}
```

---

## Cliente Externo Independiente (`Fetch API`)

En la carpeta `src/main/webapp/cliente/` se encuentra el cliente web independiente de JSF:
- `index.html`: Interfaz responsiva maquetada con **Bootstrap 5.3**.
- `style.css`: Estilos personalizados, indicadores KPI y diseño corporativo.
- `app.js`: Consumo asíncrono mediante `fetch()` hacia `/api/categorias` y `/api/productos`, renderizado de tablas dinámicas, gestión de formularios `POST`/`PUT`, eliminación `DELETE`, spinners de carga obligatorios y alertas de error/éxito dinámicas.

---

## Instrucciones de Instalación, Ejecución y Pruebas

### 1. Preparar la Base de Datos
1. Asegúrate de tener **MySQL Server** en ejecución.
2. Ejecuta el script `database/ferregest.sql` para crear la base de datos `ferregest_db`, las tablas `categorias` y `productos` y los datos iniciales.
3. Verifica las credenciales en `src/main/resources/META-INF/persistence.xml`.

### 2. Compilar y Empaquetar con Maven
```bash
mvn clean package
```
Esto generará el archivo `target/ferregest.war`.

### 3. Desplegar en Apache Tomcat (8.5 / 9.0)
Copia `target/ferregest.war` en la carpeta `webapps/` de Tomcat e inicia el servidor:
- **Cliente Externo REST (Fase 3)**: `http://localhost:8080/ferregest/cliente/index.html`
- **Endpoints API REST (Fase 3)**:
  - `http://localhost:8080/ferregest/api/categorias`
  - `http://localhost:8080/ferregest/api/productos`
- **Vistas JSF (Fase 2)**: `http://localhost:8080/ferregest/index.xhtml`

### 4. Probar con Postman
Importa el archivo `ferregest_postman_collection.json` ubicado en la raíz del proyecto dentro de **Postman** para ejecutar las pruebas de todos los endpoints (`GET`, `POST`, `PUT`, `DELETE` y casos de validación `400`/`404`).
