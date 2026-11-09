package com.ferregest.rest;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.dao.ProductoDAO;
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
 * Recurso REST para la gestión de Productos (Fase 3 - Integrante 3).
 * Expone endpoints con los verbos HTTP GET, POST, PUT y DELETE en formato JSON,
 * integrándose con ProductoDAO y CategoriaDAO con persistencia JPA/Hibernate.
 */
@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Context
    private UriInfo uriInfo;

    /**
     * GET /api/productos
     * Lista todos los productos. Admite filtrado opcional mediante parámetros de consulta:
     * - ?categoriaId={id}: filtra productos pertenecientes a esa categoría.
     * - ?buscar={termino}: filtra productos cuyo nombre coincida con el texto de búsqueda.
     *
     * @param categoriaId (Opcional) ID de categoría a filtrar
     * @param buscar      (Opcional) Término de búsqueda en el nombre
     * @return 200 OK con la lista de ProductoDTO en formato JSON
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

            List<ProductoDTO> resultado = new ArrayList<>();
            for (Producto producto : productos) {
                resultado.add(convertirDTO(producto));
            }

            return Response.ok(resultado).build();

        } catch (Exception e) {
            System.err.println("Error en GET /api/productos: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al consultar los productos", 500))
                    .build();
        }
    }

    /**
     * GET /api/productos/{id}
     * Obtiene el detalle completo de un producto por su identificador primario.
     *
     * @param id Identificador numérico del producto
     * @return 200 OK con ProductoDTO, 400 Bad Request si el ID es inválido, o 404 Not Found
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID del producto debe ser un número entero mayor que cero", 400))
                    .build();
        }

        try {
            Producto producto = productoDAO.obtenerPorId(id);

            if (producto == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró ningún producto con el ID " + id, 404))
                        .build();
            }

            return Response.ok(convertirDTO(producto)).build();

        } catch (Exception e) {
            System.err.println("Error en GET /api/productos/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al consultar el producto", 500))
                    .build();
        }
    }

    /**
     * GET /api/productos/categoria/{categoriaId}
     * Ruta explícita para listar productos filtrados por una categoría específica.
     *
     * @param categoriaId Identificador de la categoría
     * @return 200 OK con List de ProductoDTO, 400 si el ID es inválido, o 404 si la categoría no existe
     */
    @GET
    @Path("/categoria/{categoriaId}")
    public Response listarPorCategoria(@PathParam("categoriaId") int categoriaId) {
        if (categoriaId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID de la categoría debe ser mayor que cero", 400))
                    .build();
        }

        try {
            Categoria categoria = categoriaDAO.obtenerPorId(categoriaId);
            if (categoria == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró la categoría con el ID " + categoriaId, 404))
                        .build();
            }

            List<Producto> productos = productoDAO.listarPorCategoria(categoriaId);
            List<ProductoDTO> resultado = new ArrayList<>();
            for (Producto p : productos) {
                resultado.add(convertirDTO(p));
            }

            return Response.ok(resultado).build();

        } catch (Exception e) {
            System.err.println("Error en GET /api/productos/categoria/" + categoriaId + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al filtrar productos por categoría", 500))
                    .build();
        }
    }

    /**
     * GET /api/productos/buscar
     * Ruta explícita para búsqueda por nombre en tiempo real para el cliente consumidor.
     *
     * @param termino Texto a buscar en el nombre (parámetros 'nombre' o 'q')
     * @return 200 OK con List de ProductoDTO
     */
    @GET
    @Path("/buscar")
    public Response buscarPorNombre(
            @QueryParam("nombre") String nombre,
            @QueryParam("q") String q) {
        String termino = (nombre != null && !nombre.trim().isEmpty()) ? nombre : q;

        try {
            List<Producto> productos = (termino != null && !termino.trim().isEmpty())
                    ? productoDAO.buscarPorNombre(termino.trim())
                    : productoDAO.listar();

            List<ProductoDTO> resultado = new ArrayList<>();
            for (Producto p : productos) {
                resultado.add(convertirDTO(p));
            }

            return Response.ok(resultado).build();

        } catch (Exception e) {
            System.err.println("Error en GET /api/productos/buscar: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al buscar productos", 500))
                    .build();
        }
    }

    /**
     * POST /api/productos
     * Registra un nuevo producto en el catálogo.
     *
     * @param dto Datos del producto en formato JSON
     * @return 201 Created con cabecera Location y ProductoDTO, o 400 Bad Request
     */
    @POST
    public Response insertar(ProductoDTO dto) {
        Response errorValidacion = validarProducto(dto);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        try {
            Categoria categoria = null;
            if (dto.getCategoriaId() != null && dto.getCategoriaId() > 0) {
                categoria = categoriaDAO.obtenerPorId(dto.getCategoriaId());
                if (categoria == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new MensajeRespuesta(false, "La categoría indicada (ID " + dto.getCategoriaId() + ") no existe", 400))
                            .build();
                }
            }

            String descripcion = (dto.getDescripcion() != null && !dto.getDescripcion().trim().isEmpty())
                    ? dto.getDescripcion().trim()
                    : null;

            Producto producto = new Producto(
                    dto.getNombre().trim(),
                    descripcion,
                    dto.getPrecio(),
                    dto.getStock(),
                    categoria
            );

            if (!productoDAO.insertar(producto)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new MensajeRespuesta(false, "No se pudo registrar el producto en la base de datos", 500))
                        .build();
            }

            ProductoDTO respuestaDTO = convertirDTO(producto);

            URI uriCreada = (uriInfo != null)
                    ? uriInfo.getAbsolutePathBuilder().path(String.valueOf(producto.getId())).build()
                    : URI.create("/api/productos/" + producto.getId());

            return Response.created(uriCreada)
                    .entity(respuestaDTO)
                    .build();

        } catch (Exception e) {
            System.err.println("Error en POST /api/productos: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al registrar el producto", 500))
                    .build();
        }
    }

    /**
     * PUT /api/productos/{id}
     * Actualiza la información completa de un producto existente.
     *
     * @param id  Identificador único del producto
     * @param dto Nuevos valores del producto
     * @return 200 OK con ProductoDTO actualizado, 400 Bad Request o 404 Not Found
     */
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, ProductoDTO dto) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID del producto debe ser un número entero mayor que cero", 400))
                    .build();
        }

        Response errorValidacion = validarProducto(dto);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        try {
            Producto existente = productoDAO.obtenerPorId(id);

            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró ningún producto con el ID " + id, 404))
                        .build();
            }

            Categoria categoria = null;
            if (dto.getCategoriaId() != null && dto.getCategoriaId() > 0) {
                categoria = categoriaDAO.obtenerPorId(dto.getCategoriaId());
                if (categoria == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new MensajeRespuesta(false, "La categoría indicada (ID " + dto.getCategoriaId() + ") no existe", 400))
                            .build();
                }
            }

            String descripcion = (dto.getDescripcion() != null && !dto.getDescripcion().trim().isEmpty())
                    ? dto.getDescripcion().trim()
                    : null;

            existente.setNombre(dto.getNombre().trim());
            existente.setDescripcion(descripcion);
            existente.setPrecio(dto.getPrecio());
            existente.setStock(dto.getStock());
            existente.setCategoria(categoria);

            if (!productoDAO.actualizar(existente)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new MensajeRespuesta(false, "No se pudo actualizar el producto en la base de datos", 500))
                        .build();
            }

            return Response.ok(convertirDTO(existente)).build();

        } catch (Exception e) {
            System.err.println("Error en PUT /api/productos/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al actualizar el producto", 500))
                    .build();
        }
    }

    /**
     * DELETE /api/productos/{id}
     * Elimina un producto por su clave primaria.
     *
     * @param id Identificador del producto a eliminar
     * @return 200 OK con confirmación en MensajeRespuesta, 400 Bad Request o 404 Not Found
     */
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El ID del producto debe ser un número entero mayor que cero", 400))
                    .build();
        }

        try {
            Producto existente = productoDAO.obtenerPorId(id);

            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeRespuesta(false, "No se encontró ningún producto con el ID " + id, 404))
                        .build();
            }

            if (!productoDAO.eliminar(id)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new MensajeRespuesta(false, "No se pudo eliminar el producto de la base de datos", 500))
                        .build();
            }

            return Response.ok(
                    new MensajeRespuesta(true, "Producto '" + existente.getNombre() + "' (ID: " + id + ") eliminado exitosamente", 200)
            ).build();

        } catch (Exception e) {
            System.err.println("Error en DELETE /api/productos/" + id + ": " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeRespuesta(false, "Error interno al eliminar el producto", 500))
                    .build();
        }
    }

    /**
     * Valida los datos recibidos en el DTO de producto según las reglas de negocio.
     *
     * @param dto ProductoDTO a validar
     * @return Response con estado 400 si hay error de validación, o null si los datos son correctos
     */
    private Response validarProducto(ProductoDTO dto) {
        if (dto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El cuerpo de la solicitud no puede estar vacío", 400))
                    .build();
        }

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El nombre del producto es obligatorio y no puede estar vacío", 400))
                    .build();
        }

        if (dto.getNombre().trim().length() > 150) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El nombre del producto no puede exceder los 150 caracteres", 400))
                    .build();
        }

        if (dto.getPrecio() <= 0 || !Double.isFinite(dto.getPrecio())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El precio del producto debe ser un número válido mayor que cero", 400))
                    .build();
        }

        if (dto.getStock() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeRespuesta(false, "El stock disponible no puede ser un valor negativo", 400))
                    .build();
        }

        return null;
    }

    /**
     * Convierte una entidad JPA Producto a su correspondiente ProductoDTO desacoplado.
     *
     * @param producto Entidad de base de datos
     * @return ProductoDTO apto para serializar a JSON
     */
    private ProductoDTO convertirDTO(Producto producto) {
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
