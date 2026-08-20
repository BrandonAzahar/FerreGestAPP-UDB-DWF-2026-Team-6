package com.ferregest.controller;

import com.ferregest.dao.ProductoDAO;
import com.ferregest.dao.CategoriaDAO;
import com.ferregest.model.Producto;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controlador Principal para gestionar las peticiones de los Productos.
 * La ruta del servlet en el navegador será /productos
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/productos"})
public class ProductoServlet extends HttpServlet {

    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;

    /**
     * El método init() se ejecuta una sola vez cuando el Servlet nace en el servidor (Tomcat).
     * Lo usamos para instanciar el DAO y no consumir memoria repetidamente.
     */
    @Override
    public void init() throws ServletException {
        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();
    }

    /**
     * Maneja todas las peticiones tipo GET (lecturas de datos desde la barra de direcciones de la URL).
     * Por ejemplo, al ingresar a la página web o hacer click en un enlace "eliminar".
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtenemos un parámetro de la URL (ej. ?action=eliminar)
        String action = request.getParameter("action");
        if (action == null) {
            action = "listar"; // Por defecto mostramos la tabla si no hay acción
        }

        // Según el valor de action, decidimos qué función ejecutar
        switch (action) {
            case "nuevo":
                request.setAttribute("categorias", categoriaDAO.listar());
                request.getRequestDispatcher("/productos/registro.jsp").forward(request, response);
                break;
            case "editar":
                editarProducto(request, response);
                break;
            case "eliminar":
                eliminarProducto(request, response);
                break;
            case "listar":
            default:
                listarProductos(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if ("insertar".equals(action)) {
            insertarProducto(request, response);
        } else if ("actualizar".equals(action)) {
            actualizarProducto(request, response);
        } else {
            response.sendRedirect("productos");
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Producto> listaProductos = productoDAO.listar();
        request.setAttribute("productos", listaProductos);
        request.getRequestDispatcher("/productos/listado.jsp").forward(request, response);
    }

    private void editarProducto(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Producto p = productoDAO.obtenerPorId(id);
        request.setAttribute("producto", p);
        request.setAttribute("categorias", categoriaDAO.listar());
        request.getRequestDispatcher("/productos/edicion.jsp").forward(request, response);
    }

    /**
     * Recoge los datos de un formulario de inserción (vía POST) y los guarda en base de datos.
     */
    private void insertarProducto(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if (nombre == null || nombre.trim().isEmpty()) {
            request.setAttribute("error", "El nombre es obligatorio.");
            request.setAttribute("categorias", categoriaDAO.listar());
            request.getRequestDispatcher("/productos/registro.jsp").forward(request, response);
            return;
        }

        try {
            double precio = Double.parseDouble(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            int categoriaId = Integer.parseInt(request.getParameter("categoria_id"));

            if (precio <= 0 || stock < 0 || categoriaId <= 0) {
                request.setAttribute("error", "Valores numéricos inválidos.");
                request.setAttribute("categorias", categoriaDAO.listar());
                request.getRequestDispatcher("/productos/registro.jsp").forward(request, response);
                return;
            }

            Producto producto = new Producto(0, nombre, descripcion, precio, stock, categoriaId);
            productoDAO.insertar(producto);
            response.sendRedirect("productos");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Error en el formato de los números.");
            request.setAttribute("categorias", categoriaDAO.listar());
            request.getRequestDispatcher("/productos/registro.jsp").forward(request, response);
        }
    }

    /**
     * Recoge los datos actualizados de un producto desde un formulario y los envía al DAO.
     */
    private void actualizarProducto(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if (nombre == null || nombre.trim().isEmpty()) {
            request.setAttribute("error", "El nombre es obligatorio.");
            Producto prodError = new Producto(id, nombre, descripcion, 0, 0, 0);
            request.setAttribute("producto", prodError);
            request.setAttribute("categorias", categoriaDAO.listar());
            request.getRequestDispatcher("/productos/edicion.jsp").forward(request, response);
            return;
        }

        try {
            double precio = Double.parseDouble(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            int categoriaId = Integer.parseInt(request.getParameter("categoria_id"));

            if (precio <= 0 || stock < 0 || categoriaId <= 0) {
                request.setAttribute("error", "Valores numéricos inválidos.");
                Producto prodError = new Producto(id, nombre, descripcion, precio, stock, categoriaId);
                request.setAttribute("producto", prodError);
                request.setAttribute("categorias", categoriaDAO.listar());
                request.getRequestDispatcher("/productos/edicion.jsp").forward(request, response);
                return;
            }

            Producto producto = new Producto(id, nombre, descripcion, precio, stock, categoriaId);
            productoDAO.actualizar(producto);
            response.sendRedirect("productos");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Error en el formato de los números.");
            Producto prodError = new Producto(id, nombre, descripcion, 0, 0, 0);
            request.setAttribute("producto", prodError);
            request.setAttribute("categorias", categoriaDAO.listar());
            request.getRequestDispatcher("/productos/edicion.jsp").forward(request, response);
        }
    }

    /**
     * Toma un ID por parámetro GET desde la URL y lo manda eliminar.
     */
    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        productoDAO.eliminar(id);
        
        // Volvemos al listado después de eliminar
        response.sendRedirect("productos");
    }
}
