package controlador;

import conexion.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;

public class Ctrl_Categoria {

    public boolean guardar(Categoria objeto) {
        boolean respuesta = false;
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement(
                "insert into tb_categoria (descripcion, estado) values(?,?)"
            );
            consulta.setString(1, objeto.getDescripcion());
            consulta.setInt(2, objeto.getEstado());

            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al guardar categoria: " + e);
        }
        return respuesta;
    }

    public List<Categoria> obtenerTodas() {
        List<Categoria> lista = new ArrayList<>();
        Connection cn = conexion.conectar();
        try {
            PreparedStatement consulta = cn.prepareStatement("select * from tb_categoria where estado = 1");
            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("idCategoria"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setEstado(rs.getInt("estado"));
                
                lista.add(c);
            }
            cn.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener categorias: " + e);
        }
        return lista;
    }
}
