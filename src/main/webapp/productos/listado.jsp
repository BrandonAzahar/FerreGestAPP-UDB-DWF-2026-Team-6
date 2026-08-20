<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Importamos la librería principal de JSTL (Core) para usar ciclos y condicionales (sin necesidad de escribir código Java puro, es decir, cero scriptlets) -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FerreGest - Gestión de Productos</title>
    <!-- Incluimos Bootstrap 5 vía CDN para estilización rápida y moderna -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<!-- Barra de navegación principal -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand" href="#">FerreGest</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/productos">Productos</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/categorias">Categorías</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Inventario de Productos</h2>
        <!-- Botón estético (La funcionalidad de abrir un modal para un nuevo producto es para Sprints futuros) -->
        <a href="productos?action=nuevo" class="btn btn-success">Nuevo Producto</a>
    </div>

    <!-- Contenedor visual tipo "tarjeta" con sombra para destacar la tabla -->
    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover table-striped mb-0">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>ID Cat.</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- 
                         Uso de JSTL (<c:forEach>) para iterar sobre la lista de objetos 'productos' que nos mandó el Servlet (request.setAttribute).
                         El Expression Language (${producto.nombre}) es la forma limpia de acceder a los getters (getNombre()) de la clase Java en el archivo JSP.
                    --%>
                    <c:forEach var="producto" items="${productos}">
                        <tr>
                            <td>${producto.id}</td>
                            <td>${producto.nombre}</td>
                            <td>${producto.descripcion}</td>
                            <td>$${producto.precio}</td>
                            <td>${producto.stock}</td>
                            <td>${producto.categoriaId}</td>
                            <td>
                                <a href="productos?action=editar&id=${producto.id}" class="btn btn-sm btn-warning">Editar</a>
                                <!-- 
                                     Enlace para eliminar, enviando un parámetro GET llamado 'action' y el 'id' del producto. 
                                     Incluye un confirm por JavaScript simple para evitar clics accidentales.
                                -->
                                <a href="productos?action=eliminar&id=${producto.id}" class="btn btn-sm btn-danger" onclick="return confirm('¿Está seguro de eliminar este producto?');">Eliminar</a>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <%-- Condicional JSTL (<c:if>) por si el ArrayList de la BD llega vacío --%>
                    <c:if test="${empty productos}">
                        <tr>
                            <td colspan="7" class="text-center p-4">No hay productos registrados en el inventario.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Scripts interactivos de Bootstrap (Para Modales, barras laterales, menús desplegables) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
