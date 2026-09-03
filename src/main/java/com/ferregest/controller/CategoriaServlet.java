package com.ferregest.controller;

import com.ferregest.dao.CategoriaDAO;
import com.ferregest.model.Categoria;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/categorias"})
public class CategoriaServlet extends HttpServlet {

    private CategoriaDAO categoriaDAO;

    @Override
    public void init() throws ServletException {
        categoriaDAO = new CategoriaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "listar";
        }

        switch (action) {
            case "nuevo":
                request.getRequestDispatcher("/categorias/registro.jsp").forward(request, response);
                break;
            case "editar":
                editarCategoria(request, response);
                break;
            case "eliminar":
                eliminarCategoria(request, response);
                break;
            case "listar":
            default:
                listarCategorias(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");

        if ("insertar".equals(action)) {
            insertarCategoria(request, response);
        } else if ("actualizar".equals(action)) {
            actualizarCategoria(request, response);
        } else {
            response.sendRedirect("categorias");
        }
    }

    private void listarCategorias(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Categoria> lista = categoriaDAO.listar();
        request.setAttribute("categorias", lista);
        request.getRequestDispatcher("/categorias/listado.jsp").forward(request, response);
    }

    private void editarCategoria(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Categoria cat = categoriaDAO.obtenerPorId(id);
        request.setAttribute("categoria", cat);
        request.getRequestDispatcher("/categorias/edicion.jsp").forward(request, response);
    }

    private void insertarCategoria(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if (nombre == null || nombre.trim().isEmpty()) {
            request.setAttribute("error", "El nombre de la categoría es obligatorio.");
            request.getRequestDispatcher("/categorias/registro.jsp").forward(request, response);
            return;
        }

        Categoria cat = new Categoria(0, nombre, descripcion);
        categoriaDAO.insertar(cat);

        response.sendRedirect("categorias");
    }

    private void actualizarCategoria(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if (nombre == null || nombre.trim().isEmpty()) {
            request.setAttribute("error", "El nombre de la categoría es obligatorio.");
            Categoria catError = new Categoria(id, nombre, descripcion);
            request.setAttribute("categoria", catError);
            request.getRequestDispatcher("/categorias/edicion.jsp").forward(request, response);
            return;
        }

        Categoria cat = new Categoria(id, nombre, descripcion);
        categoriaDAO.actualizar(cat);

        response.sendRedirect("categorias");
    }

    private void eliminarCategoria(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        categoriaDAO.eliminar(id);
        
        response.sendRedirect("categorias");
    }
}
