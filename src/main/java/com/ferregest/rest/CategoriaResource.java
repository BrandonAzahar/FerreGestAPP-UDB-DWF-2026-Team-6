package com.ferregest.rest;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.dao.ProductoDAO;
import com.ferregest.dto.CategoriaDTO;
import com.ferregest.dto.MensajeRespuesta;
import com.ferregest.dto.ProductoDTO;
import com.ferregest.model.Categoria;
import com.ferregest.model.Producto;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST para la gestión de Categorías (Fase 3 - Integrante 3).
 * Expone endpoints con los verbos HTTP GET, POST, PUT y DELETE en formato JSON,
 * integrándose con CategoriaDAO y ProductoDAO a través de JPA/Hibernate.
 */
@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    @Context
    private UriInfo uriInfo;

    /**
     * GET /api/categorias
     * Lista todas las categorías registradas con el conteo de productos asociados.
     *
     * @return 200 OK con la lista de CategoriaDTO en formato JSON, o 500 Internal Server Error
     */
    @GET
    public Response listar() {
        try {
            List<Categoria> categorias = categoriaDAO.listar();
            List<CategoriaDTO> resultado = new ArrayList<>();

            for (Categoria categoria : categorias) {
                long totalProds = categoriaDAO.contarProductos(categoria.getId());
                resultado.add(convertirDTO(categoria, totalProds));
            }

            return Response.ok(resultado).build();
        } catch (Exception e) {
            System.err.println("Error en GET /api/categorias: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al consultar las categorías", 500))
                    .build();
        }
    }

    /**
     * GET /api/categorias/{id}
     * Obtiene los detalles de una categoría específica por su ID.
     *
     * @param id Identificador numérico de la categoría
     * @return 200 OK con CategoriaDTO, 400 Bad Request si el ID es inválido, o 404 Not Found
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID de la categoría debe ser un número entero mayor que cero", 400))
                    .build();
        }

        try {
            Categoria categoria = categoriaDAO.obtenerPorId(id);

            if (categoria == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró ninguna categoría con el ID " + id, 404))
                        .build();
            }

            long totalProds = categoriaDAO.contarProductos(categoria.getId());
            return Response.ok(convertirDTO(categoria, totalProds)).build();
        } catch (Exception e) {
            System.err.println("Error en GET /api/categorias/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al consultar la categoría", 500))
                    .build();
        }
    }

    /**
     * GET /api/categorias/{id}/productos
     * Lista todos los productos que pertenecen a una categoría específica.
     *
     * @param id Identificador de la categoría
     * @return 200 OK con List de ProductoDTO, 400 si el ID es inválido, o 404 si la categoría no existe
     */
    @GET
    @Path("/{id}/productos")
    public Response listarProductosPorCategoria(@PathParam("id") int id) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID de la categoría debe ser un número entero mayor que cero", 400))
                    .build();
        }

        try {
            Categoria categoria = categoriaDAO.obtenerPorId(id);
            if (categoria == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró la categoría con el ID " + id, 404))
                        .build();
            }

            List<Producto> productos = productoDAO.listarPorCategoria(id);
            List<ProductoDTO> resultado = new ArrayList<>();
            for (Producto p : productos) {
                resultado.add(convertirProductoDTO(p));
            }

            return Response.ok(resultado).build();
        } catch (Exception e) {
            System.err.println("Error en GET /api/categorias/" + id + "/productos: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al listar productos de la categoría", 500))
                    .build();
        }
    }

    /**
     * POST /api/categorias
     * Registra una nueva categoría en el sistema.
     *
     * @param dto Datos de la categoría enviada en el cuerpo JSON
     * @return 201 Created con cabecera Location y el CategoriaDTO creado, 400 Bad Request o 409 Conflict
     */
    @POST
    public Response insertar(CategoriaDTO dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El nombre de la categoría es obligatorio y no puede estar vacío", 400))
                    .build();
        }

        String nombre = dto.getNombre().trim();

        if (nombre.length() > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El nombre de la categoría no puede exceder los 100 caracteres", 400))
                    .build();
        }

        try {
            if (categoriaDAO.existeNombre(nombre, 0)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new MensajeRespuesta(false, "Ya existe una categoría registrada con el nombre '" + nombre + "'", 409))
                        .build();
            }

            String descripcion = (dto.getDescripcion() != null && !dto.getDescripcion().trim().isEmpty())
                    ? dto.getDescripcion().trim()
                    : null;

            Categoria categoria = new Categoria(nombre, descripcion);

            if (!categoriaDAO.insertar(categoria)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new MensajeRespuesta(false, "No se pudo registrar la categoría en la base de datos", 500))
                        .build();
            }

            CategoriaDTO respuesta = convertirDTO(categoria, 0L);

            URI uriCreada = (uriInfo != null)
                    ? uriInfo.getAbsolutePathBuilder().path(String.valueOf(categoria.getId())).build()
                    : URI.create("/api/categorias/" + categoria.getId());

            return Response.created(uriCreada)
                    .entity(respuesta)
                    .build();

        } catch (Exception e) {
            System.err.println("Error en POST /api/categorias: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al registrar la categoría", 500))
                    .build();
        }
    }

    /**
     * PUT /api/categorias/{id}
     * Actualiza una categoría existente.
     *
     * @param id  Identificador de la categoría a actualizar
     * @param dto Datos actualizados de la categoría
     * @return 200 OK con CategoriaDTO actualizado, 400 Bad Request, 404 Not Found o 409 Conflict
     */
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, CategoriaDTO dto) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID de la categoría debe ser un número entero mayor que cero", 400))
                    .build();
        }

        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El nombre de la categoría es obligatorio", 400))
                    .build();
        }

        String nombre = dto.getNombre().trim();

        if (nombre.length() > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El nombre no puede exceder los 100 caracteres", 400))
                    .build();
        }

        try {
            Categoria existente = categoriaDAO.obtenerPorId(id);

            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró la categoría con el ID " + id, 404))
                        .build();
            }

            if (categoriaDAO.existeNombre(nombre, id)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new MensajeRespuesta(false, "Ya existe otra categoría registrada con el nombre '" + nombre + "'", 409))
                        .build();
            }

            String descripcion = (dto.getDescripcion() != null && !dto.getDescripcion().trim().isEmpty())
                    ? dto.getDescripcion().trim()
                    : null;

            existente.setNombre(nombre);
            existente.setDescripcion(descripcion);

            if (!categoriaDAO.actualizar(existente)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new MensajeRespuesta(false, "No se pudo actualizar la categoría en la base de datos", 500))
                        .build();
            }

            long totalProds = categoriaDAO.contarProductos(existente.getId());
            return Response.ok(convertirDTO(existente, totalProds)).build();

        } catch (Exception e) {
            System.err.println("Error en PUT /api/categorias/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al actualizar la categoría", 500))
                    .build();
        }
    }

    /**
     * DELETE /api/categorias/{id}
     * Elimina una categoría por su ID. Desvincula de forma segura los productos asignados.
     *
     * @param id Identificador de la categoría a eliminar
     * @return 200 OK con MensajeRespuesta descriptivo, 400 Bad Request o 404 Not Found
     */
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID de la categoría debe ser un número entero mayor que cero", 400))
                    .build();
        }

        try {
            Categoria existente = categoriaDAO.obtenerPorId(id);

            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró la categoría con el ID " + id, 404))
                        .build();
            }

            long prodsAfectados = categoriaDAO.contarProductos(id);

            if (!categoriaDAO.eliminar(id)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new MensajeRespuesta(false, "No se pudo eliminar la categoría de la base de datos", 500))
                        .build();
            }

            String detalle = prodsAfectados > 0
                    ? "Categoría '" + existente.getNombre() + "' eliminada exitosamente. " + prodsAfectados + " producto(s) quedaron sin categoría."
                    : "Categoría '" + existente.getNombre() + "' eliminada exitosamente.";

            return Response.ok(new MensajeRespuesta(true, detalle, 200)).build();

        } catch (Exception e) {
            System.err.println("Error en DELETE /api/categorias/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al eliminar la categoría", 500))
                    .build();
        }
    }

    // Métodos auxiliares de conversión a DTO
    private CategoriaDTO convertirDTO(Categoria categoria, Long totalProductos) {
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                totalProductos
        );
    }

    private ProductoDTO convertirProductoDTO(Producto producto) {
        Integer categoriaId = null;
        String categoriaNombre = null;

        if (producto.getCategoria() != null) {
            categoriaId = producto.getCategoria().getId();
            categoriaNombre = producto.getCategoria().getNombre();
        }

        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                categoriaId,
                categoriaNombre
        );
    }
}
