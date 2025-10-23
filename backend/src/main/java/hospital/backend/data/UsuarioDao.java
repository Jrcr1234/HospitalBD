package hospital.backend.data;

import hospital.protocol.logic.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    // Se obtiene la instancia de la base de datos para usar la conexión
    Database db = Database.getInstance();

    /**
     * Lee un usuario de la base de datos según su ID y clave.
     * Esencial para la funcionalidad de login.
     *
     * @param id El ID del usuario a buscar.
     * @param clave La clave del usuario.
     * @return El objeto Usuario si se encuentra, de lo contrario null.
     * @throws Exception Si ocurre un error de SQL.
     */

    public Usuario readById(String id) throws Exception {
        String sql = "SELECT * FROM Usuario WHERE id = ?"; // Busca solo por ID
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return from(rs); // Reutiliza el método 'from'
            } else {
                return null; // No encontrado
            }
        } catch (SQLException ex) {
            throw new Exception("Error al leer el usuario por ID", ex);
        }
    }
    public Usuario read(String id, String clave) throws Exception {
        // 1. Sentencia SQL para la consulta
        String sql = "SELECT * FROM Usuario WHERE id = ? AND clave = ?";

        try {
            // 2. Preparar la consulta
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);       // Asigna el primer '?' con el id
            stm.setString(2, clave);    // Asigna el segundo '?' con la clave

            // 3. Ejecutar la consulta y obtener los resultados
            ResultSet rs = db.executeQuery(stm);

            // 4. Procesar los resultados
            if (rs.next()) {
                // Si hay un resultado, crea el objeto Usuario
                return from(rs);
            } else {
                // Si no hay resultados, las credenciales son incorrectas
                return null;
            }
        } catch (SQLException ex) {
            // Lanza una excepción si algo sale mal con la consulta
            throw new Exception("Error al leer el usuario", ex);
        }
    }

    /**
     * Actualiza únicamente la clave de un usuario específico.
     * @param id El ID del usuario cuya clave se actualizará.
     * @param nuevaClave La nueva clave a establecer.
     * @throws Exception Si ocurre un error de SQL o el usuario no existe.
     */
    public void updatePassword(String id, String nuevaClave) throws Exception {
        String sql = "UPDATE Usuario SET clave = ? WHERE id = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, nuevaClave); // Nueva clave
            stm.setString(2, id);         // ID del usuario a actualizar

            int count = db.executeUpdate(stm);
            if (count == 0) {
                // Esto no debería pasar si la verificación previa en el Service funcionó,
                // pero es una buena validación.
                throw new Exception("No se pudo actualizar la clave, usuario no encontrado.");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al actualizar la clave del usuario: " + ex.getMessage());
        }
    }

    /**
     * Método de utilidad para convertir un ResultSet a un objeto Usuario.
     *
     * @param rs El ResultSet de la consulta SQL.
     * @return Un objeto Usuario con los datos del ResultSet.
     * @throws SQLException Si hay un error al acceder a los datos del ResultSet.
     */
    private Usuario from(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getString("id"));
        u.setClave(rs.getString("clave"));
        u.setNombre(rs.getString("nombre"));
        u.setTipo(rs.getString("tipo"));
        return u;
    }
}