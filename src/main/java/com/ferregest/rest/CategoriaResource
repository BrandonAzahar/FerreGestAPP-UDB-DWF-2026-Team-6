package com.ferregest.rest;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.dto.CategoriaDTO;
import com.ferregest.model.Categoria;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    // GET: Listar todas las categorías
    @GET
    public Response listar() {

        try {
            List<Categoria> categorias = categoriaDAO.listar();
            List<CategoriaDTO> resultado = new ArrayList<>();

            for (Categoria categoria : categorias) {
                resultado.add(convertirDTO(categoria));
            }

            return Response.ok(resultado).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error al consultar las categorías")
                    .build();
        }
    }

    // GET: Obtener una categoría por ID
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") int id) {

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID debe ser mayor que cero")
                    .build();
        }

        try {
            Categoria categoria = categoriaDAO.obtenerPorId(id);

            if (categoria == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Categoría no encontrada")
                        .build();
            }

            return Response.ok(convertirDTO(categoria)).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error al consultar la categoría")
                    .build();
        }
    }

    // POST: Registrar una categoría
    @POST
    public Response insertar(CategoriaDTO dto) {

        if (dto == null || dto.getNombre() == null
                || dto.getNombre().trim().isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre de la categoría es obligatorio")
                    .build();
        }

        String nombre = dto.getNombre().trim();

        if (nombre.length() > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre no puede superar los 100 caracteres")
                    .build();
        }

        try {
            if (categoriaDAO.existeNombre(nombre, 0)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Ya existe una categoría con ese nombre")
                        .build();
            }

            Categoria categoria = new Categoria(
                    nombre,
                    dto.getDescripcion()
            );

            if (!categoriaDAO.insertar(categoria)) {
                return Response.serverError()
                        .entity("No se pudo registrar la categoría")
                        .build();
            }

            CategoriaDTO respuesta = convertirDTO(categoria);

            return Response.status(Response.Status.CREATED)
                    .entity(respuesta)
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error al registrar la categoría")
                    .build();
        }
    }

    // PUT: Actualizar una categoría
    @PUT
    @Path("/{id}")
    public Response actualizar(
            @PathParam("id") int id,
            CategoriaDTO dto) {

        if (id <= 0 || dto == null
                || dto.getNombre() == null
                || dto.getNombre().trim().isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos de categoría inválidos")
                    .build();
        }

        String nombre = dto.getNombre().trim();

        if (nombre.length() > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre no puede superar los 100 caracteres")
                    .build();
        }

        try {
            Categoria existente = categoriaDAO.obtenerPorId(id);

            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Categoría no encontrada")
                        .build();
            }

            if (categoriaDAO.existeNombre(nombre, id)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Ya existe otra categoría con ese nombre")
                        .build();
            }

            existente.setNombre(nombre);
            existente.setDescripcion(dto.getDescripcion());

            if (!categoriaDAO.actualizar(existente)) {
                return Response.serverError()
                        .entity("No se pudo actualizar la categoría")
                        .build();
            }

            return Response.ok(convertirDTO(existente)).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error al actualizar la categoría")
                    .build();
        }
    }

    // DELETE: Eliminar una categoría
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") int id) {

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID debe ser mayor que cero")
                    .build();
        }

        try {
            Categoria existente = categoriaDAO.obtenerPorId(id);

            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Categoría no encontrada")
                        .build();
            }

            if (!categoriaDAO.eliminar(id)) {
                return Response.serverError()
                        .entity("No se pudo eliminar la categoría")
                        .build();
            }

            return Response.noContent().build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error al eliminar la categoría")
                    .build();
        }
    }

    // Conversión de entidad JPA a DTO
    private CategoriaDTO convertirDTO(Categoria categoria) {

        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}
