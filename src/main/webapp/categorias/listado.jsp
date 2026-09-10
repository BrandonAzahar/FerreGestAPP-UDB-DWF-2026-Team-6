<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FerreGest - Listado de Categorías</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/productos">FerreGest</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/productos">Productos</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/categorias">Categorías</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Listado de Categorías</h2>
        <a href="categorias?action=nuevo" class="btn btn-success">Nueva Categoría</a>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover table-striped mb-0">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${categorias}">
                        <tr>
                            <td>${cat.id}</td>
                            <td>${cat.nombre}</td>
                            <td>${cat.descripcion}</td>
                            <td>
                                <a href="categorias?action=editar&id=${cat.id}" class="btn btn-sm btn-warning">Editar</a>
                                <a href="categorias?action=eliminar&id=${cat.id}" class="btn btn-sm btn-danger" onclick="return confirm('¿Está seguro de eliminar esta categoría?');">Eliminar</a>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty categorias}">
                        <tr>
                            <td colspan="4" class="text-center p-4">No hay categorías registradas.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
