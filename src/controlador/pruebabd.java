package controlador;

import modelo.Usuario;
import java.util.List;

public class pruebabd {

    public static void main(String[] args) {

        UsuarioDAO dao = new UsuarioDAO();

        // PRUEBA CONEXIÓN
        System.out.println("Probando conexión...");
        dao.listarUsuarios();

        //  CREAR USUARIO
        Usuario u = new Usuario();
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setUsuario("juan123");
        u.setPassword("1234");
        u.setTelefono("3001234567");
        u.setEstado(1);

        dao.crearUsuario(u);

        //  LISTAR USUARIOS
        List<Usuario> lista = dao.listarUsuarios();
        for (Usuario user : lista) {
            System.out.println(user.getNombre() + " " + user.getApellido());
        }

        // ACTUALIZAR
        u.setIdUsuario(1); // cambia el ID según tu BD
        u.setNombre("Juan Actualizado");
        dao.actualizarUsuario(u);

        // BUSCAR
        Usuario encontrado = dao.buscarUsuario(1);
        System.out.println("Usuario encontrado: " + encontrado.getNombre());

        //  ELIMINAR
        dao.eliminarUsuario(1);
    }
}