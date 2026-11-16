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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST para la gestión de Productos (Fase 3 - Sprint 3).
 * Expone endpoints con JAX-RS (Jersey) y serialización JSON mediante Jackson,
 * reutilizando ProductoDAO y CategoriaDAO con JPA/Hibernate.
 */
@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    /**
     * GET /api/productos
     * Lista todos los productos del inventario, permitiendo filtrar opcionalmente
     * por categoría (?categoriaId=) o por término de búsqueda (?buscar=).
     *
     * @param categoriaId ID opcional de la categoría para filtrar
     * @param buscar      Término opcional de búsqueda por nombre
     * @return Response 200 (OK) con la lista de productos o 500 (Internal Server Error)
     */
    @GET
    public Response listar(
            @QueryParam("categoriaId") Integer categoriaId,
            @QueryParam("buscar") String buscar) {
        try {
            List<Producto> productos;
            if (categoriaId != null && categoriaId > 0) {
                productos = productoDAO.listarPorCategoria(categoriaId);
            } else if (buscar != null && !buscar.trim().isEmpty()) {
                productos = productoDAO.buscarPorNombre(buscar.trim());
            } else {
                productos = productoDAO.listar();
            }
            return Response.status(Response.Status.OK).entity(productos).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al listar los productos: " + e.getMessage());
        }
    }

    /**
     * GET /api/productos/{id}
     * Obtiene un producto específico por su identificador único.
     *
     * @param id ID del producto
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID del producto debe ser un número entero mayor que cero.");
        }

        try {
            Producto producto = productoDAO.obtenerPorId(id);
            if (producto == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró el producto con ID " + id + ".");
            }
            return Response.status(Response.Status.OK).entity(producto).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al consultar el producto: " + e.getMessage());
        }
    }

    /**
     * POST /api/productos
     * Registra un nuevo producto validando que no contenga datos vacíos o fuera de rango.
     *
     * @param producto Entidad Producto deserializada desde JSON
     * @return Response 201 (Created), 400 (Bad Request) o 500 (Internal Server Error)
     */
    @POST
    public Response insertar(Producto producto) {
        Response errorValidacion = validarDatosProducto(producto);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        try {
            Integer catId = producto.getCategoriaId();
            if (catId != null && catId > 0) {
                Categoria categoria = categoriaDAO.obtenerPorId(catId);
                if (categoria == null) {
                    return crearRespuestaError(Response.Status.BAD_REQUEST,
                            "La categoría especificada con ID " + catId + " no existe.");
                }
                producto.setCategoria(categoria);
            } else {
                producto.setCategoria(null);
            }

            producto.setNombre(producto.getNombre().trim());
            if (producto.getDescripcion() != null) {
                String desc = producto.getDescripcion().trim();
                producto.setDescripcion(desc.isEmpty() ? null : desc);
            }

            boolean insertado = productoDAO.insertar(producto);
            if (!insertado) {
                return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No fue posible guardar el producto en la base de datos.");
            }

            return Response.status(Response.Status.CREATED).entity(producto).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al registrar el producto: " + e.getMessage());
        }
    }

    /**
     * PUT /api/productos/{id}
     * Actualiza un producto existente por su identificador.
     *
     * @param id       ID del producto a actualizar
     * @param producto Datos actualizados del producto
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, Producto producto) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID del producto debe ser un número entero mayor que cero.");
        }

        Response errorValidacion = validarDatosProducto(producto);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        try {
            Producto existente = productoDAO.obtenerPorId(id);
            if (existente == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró el producto con ID " + id + " para actualizar.");
            }

            Integer catId = producto.getCategoriaId();
            if (catId != null && catId > 0) {
                Categoria categoria = categoriaDAO.obtenerPorId(catId);
                if (categoria == null) {
                    return crearRespuestaError(Response.Status.BAD_REQUEST,
                            "La categoría especificada con ID " + catId + " no existe.");
                }
                existente.setCategoria(categoria);
            } else {
                existente.setCategoria(null);
            }

            existente.setNombre(producto.getNombre().trim());
            if (producto.getDescripcion() != null) {
                String desc = producto.getDescripcion().trim();
                existente.setDescripcion(desc.isEmpty() ? null : desc);
            } else {
                existente.setDescripcion(null);
            }
            existente.setPrecio(producto.getPrecio());
            existente.setStock(producto.getStock());

            boolean actualizado = productoDAO.actualizar(existente);
            if (!actualizado) {
                return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No fue posible actualizar el producto en la base de datos.");
            }

            return Response.status(Response.Status.OK).entity(existente).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al actualizar el producto: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/productos/{id}
     * Elimina un producto existente por su identificador.
     *
     * @param id ID del producto a eliminar
     * @return Response 200 (OK), 400 (Bad Request), 404 (Not Found) o 500 (Internal Server Error)
     */
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (id <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El ID del producto debe ser un número entero mayor que cero.");
        }

        try {
            Producto existente = productoDAO.obtenerPorId(id);
            if (existente == null) {
                return crearRespuestaError(Response.Status.NOT_FOUND,
                        "No se encontró el producto con ID " + id + " para eliminar.");
            }

            boolean eliminado = productoDAO.eliminar(id);
            if (!eliminado) {
                return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No fue posible eliminar el producto de la base de datos.");
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("codigo", Response.Status.OK.getStatusCode());
            respuesta.put("mensaje", "Producto '" + existente.getNombre() + "' eliminado correctamente.");
            return Response.status(Response.Status.OK).entity(respuesta).build();
        } catch (Exception e) {
            return crearRespuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno del servidor al eliminar el producto: " + e.getMessage());
        }
    }

    /**
     * Valida que los campos obligatorios del producto no estén vacíos y cumplan las reglas de negocio.
     */
    private Response validarDatosProducto(Producto producto) {
        if (producto == null) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "Los datos del producto son obligatorios y no pueden estar vacíos.");
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El nombre del producto es obligatorio y no puede estar vacío.");
        }

        if (producto.getNombre().trim().length() > 150) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El nombre del producto no puede exceder los 150 caracteres.");
        }

        if (Double.isNaN(producto.getPrecio()) || Double.isInfinite(producto.getPrecio()) || producto.getPrecio() <= 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El precio del producto debe ser un valor numérico mayor que cero.");
        }

        if (producto.getStock() < 0) {
            return crearRespuestaError(Response.Status.BAD_REQUEST,
                    "El stock del producto no puede ser negativo.");
        }

        return null;
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
