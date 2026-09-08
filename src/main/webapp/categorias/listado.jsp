<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>FerreGest - Editar Categoría</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

</head>

<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">

    <div class="container">

        <a class="navbar-brand"
           href="${pageContext.request.contextPath}/categorias">
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
                        Editar Categoría
                    </h4>

                </div>

                <div class="card-body">

                    <form action="${pageContext.request.contextPath}/categorias"
                          method="POST"
                          onsubmit="return validarFormulario();">

                        <input type="hidden"
                               name="action"
                               value="actualizar">

                        <input type="hidden"
                               name="id"
                               value="${categoria.id}">

                        <div class="mb-3">

                            <label for="nombre"
                                   class="form-label">
                                Nombre de Categoría *
                            </label>

                            <input type="text"
                                   class="form-control"
                                   id="nombre"
                                   name="nombre"
                                   value="${categoria.nombre}"
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
                                      maxlength="255">${categoria.descripcion}</textarea>

                        </div>

                        <div class="d-grid gap-2">

                            <button type="submit"
                                    class="btn btn-primary">
                                Actualizar
                            </button>

                            <a href="${pageContext.request.contextPath}/categorias"
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

    const nombre = document.getElementById('nombre').value.trim();

    if (nombre === '') {

        alert('El nombre es obligatorio.');

        return false;
    }

    return true;
}

</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js">
</script>

</body>
</html>
