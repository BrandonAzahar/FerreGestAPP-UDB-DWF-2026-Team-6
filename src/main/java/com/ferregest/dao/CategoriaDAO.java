package com.ferregest.dao;

import com.ferregest.model.Categoria;
import com.ferregest.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        
        // OPTIMIZACIÓN: try-with-resources cierra automáticamente la conexión (conn), 
        // la sentencia (ps) y los resultados (rs) sin necesidad de bloques finally.
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Categoria cat = new Categoria(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                lista.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Categoria obtenerPorId(int id) {
        Categoria cat = null;
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cat = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cat;
    }

    public boolean insertar(Categoria cat) {
        String sql = "INSERT INTO categorias (nombre, descripcion) VALUES (?, ?)";
        boolean exito = false;
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, cat.getNombre());
            ps.setString(2, cat.getDescripcion());
            
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }

    public boolean actualizar(Categoria cat) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id = ?";
        boolean exito = false;
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, cat.getNombre());
            ps.setString(2, cat.getDescripcion());
            ps.setInt(3, cat.getId());
            
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        boolean exito = false;
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, id);
            exito = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }
}
