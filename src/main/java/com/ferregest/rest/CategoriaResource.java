package com.ferregest.rest;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.dao.ProductoDAO;
import com.ferregest.model.Categoria;
import com.ferregest.model.Producto;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST para la gestión de Categorías (Fase 3 - Sprint 3).
 * Expone endpoints con JAX-RS (Jersey) y serialización JSON mediante Jackson,
 * reutilizando CategoriaDAO y ProductoDAO con JPA/Hibernate.
 */
@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    /**
     * GET /api/categorias
     * Obtiene la lista completa de categorías registradas.
     *
     * @return Response 200 (OK) con la lista de categorías o 500 (Internal Server Error)
     */
    @GET
    public Response listar() {
        try {
            List<Categoria> categorias = categoriaDAO.listar();
            return Response.status(Response.Status.OK).entity(categorias).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al listar las categorías: " + e.getMessage());
        }
    }

    /**
     * GET /api/categorias/{id}
     * Busca una categoría específica por su identificador.
     *
     * @param id ID de la categoría
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID de la categoría debe ser un número entero mayor que cero.");
        }

        try {
            Categoria categoria = categoriaDAO.obtenerPorId(id);
            if (categoria == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró la categoría con ID " + id + ".");
            }
            return Response.status(Response.Status.OK).entity(categoria).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al consultar la categoría: " + e.getMessage());
        }
    }

    /**
     * GET /api/categorias/{id}/productos
     * Lista todos los productos asociados a una categoría específica.
     *
     * @param id ID de la categoría
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @GET
    @Path("/{id}/productos")
    public Response listarProductosPorCategoria(@PathParam("id") int id) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID de la categoría debe ser un número entero mayor que cero.");
        }

        try {
            Categoria categoria = categoriaDAO.obtenerPorId(id);
            if (categoria == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró la categoría con ID " + id + ".");
            }
            List<Producto> productos = productoDAO.listarPorCategoria(id);
            return Response.status(Response.Status.OK).entity(productos).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno al listar los productos de la categoría: " + e.getMessage());
        }
    }

    /**
     * POST /api/categorias
     * Crea una nueva categoría validando que los datos obligatorios no estén vacíos.
     *
     * @param categoria Entidad Categoria deserializada desde JSON
     * @return Response 201 (Created), 400 (Bad Request) o 500 (Internal Server Error)
     */
    @POST
    public Response insertar(Categoria categoria) {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El nombre de la categoría es obligatorio y no puede estar vacío.");
        }

        String nombreLimpio = categoria.getNombre().trim();
        if (nombreLimpio.length() > 100) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El nombre de la categoría no puede superar los 100 caracteres.");
        }

        try {
            if (categoriaDAO.existeNombre(nombreLimpio, 0)) {
                return crearRespuestaError(Response.Status.BAD_REQUEST,
                        "Ya existe una categoría registrada con el nombre '" + nombreLimpio + "'.");
            }

            categoria.setNombre(nombreLimpio);
            if (categoria.getDescripcion() != null) {
                String desc = categoria.getDescripcion().trim();
                categoria.setDescripcion(desc.isEmpty() ? null : desc);
            }

            boolean insertado = categoriaDAO.insertar(categoria);
            if (!insertado) {
                return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No fue posible guardar la categoría en la base de datos.");
            }

            return Response.status(Response.Status.CREATED).entity(categoria).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al crear la categoría: " + e.getMessage());
        }
    }

    /**
     * PUT /api/categorias/{id}
     * Actualiza una categoría existente por su identificador.
     *
     * @param id        ID de la categoría a modificar
     * @param categoria Datos actualizados de la categoría
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, Categoria categoria) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID de la categoría debe ser un número entero mayor que cero.");
        }

        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El nombre de la categoría es obligatorio y no puede estar vacío.");
        }

        String nombreLimpio = categoria.getNombre().trim();
        if (nombreLimpio.length() > 100) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El nombre de la categoría no puede superar los 100 caracteres.");
        }

        try {
            Categoria existente = categoriaDAO.obtenerPorId(id);
            if (existente == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró la categoría con ID " + id + " para actualizar.");
            }

            if (categoriaDAO.existeNombre(nombreLimpio, id)) {
                return crearRespuestaError(Response.Status.BAD_REQUEST,
                        "Ya existe otra categoría con el nombre '" + nombreLimpio + "'.");
            }

            existente.setNombre(nombreLimpio);
            if (categoria.getDescripcion() != null) {
                String desc = categoria.getDescripcion().trim();
                existente.setDescripcion(desc.isEmpty() ? null : desc);
            } else {
                existente.setDescripcion(null);
            }

            boolean actualizado = categoriaDAO.actualizar(existente);
            if (!actualizado) {
                return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No fue posible actualizar la categoría en la base de datos.");
            }

            return Response.status(Response.Status.OK).entity(existente).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al actualizar la categoría: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/categorias/{id}
     * Elimina una categoría existente por su identificador.
     *
     * @param id ID de la categoría a eliminar
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID de la categoría debe ser un número entero mayor que cero.");
        }

        try {
            Categoria existente = categoriaDAO.obtenerPorId(id);
            if (existente == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró la categoría con ID " + id + " para eliminar.");
            }

            boolean eliminado = categoriaDAO.eliminar(id);
            if (!eliminado) {
                return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No fue posible eliminar la categoría de la base de datos.");
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("codigo", Response.Status.OK.getStatusCode());
            respuesta.put("mensaje", "Categoría '" + existente.getNombre() + "' eliminada correctamente.");
            return Response.status(Response.Status.OK).entity(respuesta).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al eliminar la categoría: " + e.getMessage());
        }
    }

    /**
     * Construye una respuesta HTTP estandarizada en formato JSON para errores.
     */
    private Response crearRespuestaError(Response.Status status, String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("exito", false);
        error.put("codigo", status.getStatusCode());
        error.put("mensaje", mensaje);
        error.put("error", mensaje);
        return Response.status(status).entity(error).build();
    }
}
