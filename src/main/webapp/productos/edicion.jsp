<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>FerreGest - Editar Producto</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">

    <div class="container">

        <a class="navbar-brand"
           href="${pageContext.request.contextPath}/productos">
            FerreGest
        </a>

    </div>

</nav>

<div class="container">

    <div class="row justify-content-center">

        <div class="col-md-6">

            <div class="card shadow-sm">

                <div class="card-header bg-warning text-dark">

                    <h4 class="mb-0">
                        Editar Producto
                    </h4>

                </div>

                <div class="card-body">

                    <form action="${pageContext.request.contextPath}/productos"
                          method="POST"
                          onsubmit="return validarFormulario();">

                        <input type="hidden"
                               name="action"
                               value="actualizar">

                        <input type="hidden"
                               name="id"
                               value="${producto.id}">

                        <div class="mb-3">

                            <label for="nombre"
                                   class="form-label">
                                Nombre del Producto *
                            </label>

                            <input type="text"
                                   class="form-control"
                                   id="nombre"
                                   name="nombre"
                                   value="${producto.nombre}"
                                   required
                                   maxlength="100">

                        </div>

                        <div class="mb-3">

                            <label for="descripcion"
                                   class="form-label">
                                Descripción
                            </label>

                            <textarea class="form-control"
                                      id="descripcion"
                                      name="descripcion"
                                      rows="3"
                                      maxlength="255">${producto.descripcion}</textarea>

                        </div>

                        <div class="mb-3">

                            <label for="precio"
                                   class="form-label">
                                Precio *
                            </label>

                            <input type="number"
                                   step="0.01"
                                   class="form-control"
                                   id="precio"
                                   name="precio"
                                   value="${producto.precio}"
                                   required
                                   min="0.01">

                        </div>

                        <div class="mb-3">

                            <label for="stock"
                                   class="form-label">
                                Stock *
                            </label>

                            <input type="number"
                                   class="form-control"
                                   id="stock"
                                   name="stock"
                                   value="${producto.stock}"
                                   required
                                   min="0">

                        </div>

                        <div class="mb-3">

                            <label for="categoria_id"
                                   class="form-label">
                                Categoría *
                            </label>

                            <select class="form-select"
                                    id="categoria_id"
                                    name="categoria_id"
                                    required>

                                <option value="">
                                    Seleccione una categoría
                                </option>

                                <c:forEach var="cat"
                                           items="${categorias}">

                                    <option value="${cat.id}"
                                            ${cat.id == producto.categoriaId ? 'selected' : ''}>
                                        ${cat.nombre}
                                    </option>

                                </c:forEach>

                            </select>

                        </div>

                        <div class="d-grid gap-2">

                            <button type="submit"
                                    class="btn btn-primary">
                                Actualizar
                            </button>

                            <a href="${pageContext.request.contextPath}/productos"
                               class="btn btn-secondary">
                                Cancelar
                            </a>

                        </div>

                    </form>

                </div>

            </div>

        </div>

    </div>

</div>

<script>

function validarFormulario() {

    const nombre =
        document.getElementById('nombre').value.trim();

    const precio =
        parseFloat(document.getElementById('precio').value);

    const stock =
        parseInt(document.getElementById('stock').value);

    const categoria =
        document.getElementById('categoria_id').value;

    if (nombre === '') {

        alert('El nombre es obligatorio.');

        return false;
    }

    if (isNaN(precio) || precio <= 0) {

        alert('El precio debe ser mayor a 0.');

        return false;
    }

    if (isNaN(stock) || stock < 0) {

        alert('El stock no puede ser negativo.');

        return false;
    }

    if (categoria === '') {

        alert('Debe seleccionar una categoría.');

        return false;
    }

    return true;
}

</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js">
</script>

</body>
</html>
