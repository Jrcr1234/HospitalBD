package hospital.backend.data;

import hospital.protocol.logic.Farmaceuta; // Importa la entidad desde protocol
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Farmaceuta.
 * Interactúa con la tabla 'Usuario' filtrando por tipo='Farmaceuta'.
 */
public class FarmaceutaDao {

    Database db = Database.getInstance(); // Conexión Singleton

    // --- Métodos CRUD ---

    /**
     * Inserta un nuevo Farmaceuta en la tabla Usuario.
     * Asigna el ID como clave por defecto si no se proporciona una.
     * @param far El objeto Farmaceuta a insertar.
     * @throws Exception Si ocurre un error de SQL o el ID ya existe.
     */
    public void create(Farmaceuta far) throws Exception {
        String sql = "INSERT INTO Usuario (id, clave, nombre, tipo) VALUES (?, ?, ?, 'Farmaceuta')";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, far.getId());
            // Si la clave viene vacía o nula, usar el ID como clave por defecto
            String clave = (far.getClave() != null && !far.getClave().isEmpty()) ? far.getClave() : far.getId();
            stm.setString(2, clave);
            stm.setString(3, far.getNombre());
            // El tipo 'Farmaceuta' ya está fijo en la consulta

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Farmaceuta no creado, filas afectadas = 0");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al crear el farmaceuta: " + ex.getMessage());
        }
    }

    /**
     * Busca un Farmaceuta por su ID.
     * @param id El ID del farmaceuta a buscar.
     * @return El objeto Farmaceuta si se encuentra y es de tipo 'Farmaceuta', null si no.
     * @throws Exception Si ocurre un error de SQL.
     */
    public Farmaceuta read(String id) throws Exception {
        String sql = "SELECT * FROM Usuario WHERE id = ? AND tipo = 'Farmaceuta'";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return from(rs); // Convierte la fila a objeto Farmaceuta
            } else {
                return null; // No encontrado o no era Farmaceuta
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar el farmaceuta: " + ex.getMessage());
        }
    }

    /**
     * Actualiza el nombre de un Farmaceuta existente.
     * @param far El objeto Farmaceuta con la información actualizada (usa el ID para identificar).
     * @throws Exception Si ocurre un error de SQL o el ID no existe o no es de tipo 'Farmaceuta'.
     */
    public void update(Farmaceuta far) throws Exception {
        String sql = "UPDATE Usuario SET nombre = ? WHERE id = ? AND tipo = 'Farmaceuta'";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, far.getNombre());
            stm.setString(2, far.getId()); // Condición WHERE

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Farmaceuta no actualizado, ID no existe o no es Farmaceuta");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al actualizar el farmaceuta: " + ex.getMessage());
        }
    }

    /**
     * Elimina un Farmaceuta (un registro de Usuario con tipo='Farmaceuta') por su ID.
     * @param id El ID del farmaceuta a eliminar.
     * @throws Exception Si ocurre un error de SQL o el ID no existe o no es de tipo 'Farmaceuta'.
     */
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Usuario WHERE id = ? AND tipo = 'Farmaceuta'";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Farmaceuta no eliminado, ID no existe o no es Farmaceuta");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al eliminar el farmaceuta: " + ex.getMessage());
        }
    }

    // --- Métodos de Búsqueda ---

    /**
     * Devuelve una lista con todos los Farmaceutas registrados.
     * @return Una lista de objetos Farmaceuta.
     * @throws Exception Si ocurre un error de SQL.
     */
    public List<Farmaceuta> findAll() throws Exception {
        List<Farmaceuta> resultado = new ArrayList<>();
        String sql = "SELECT * FROM Usuario WHERE tipo = 'Farmaceuta' ORDER BY nombre";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs));
            }
            return resultado;
        } catch (SQLException ex) {
            throw new Exception("Error al obtener todos los farmaceutas: " + ex.getMessage());
        }
    }

    /**
     * Busca Farmaceutas cuyo ID o nombre contenga el filtro (ignorando mayúsculas/minúsculas).
     * @param filtro El texto a buscar en ID o nombre.
     * @return Una lista de objetos Farmaceuta que coinciden.
     * @throws Exception Si ocurre un error de SQL.
     */
    public List<Farmaceuta> search(String filtro) throws Exception {
        List<Farmaceuta> resultado = new ArrayList<>();
        String sql = "SELECT * FROM Usuario WHERE tipo = 'Farmaceuta' AND (LOWER(id) LIKE ? OR LOWER(nombre) LIKE ?) ORDER BY nombre";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            String filtroLike = "%" + filtro.toLowerCase() + "%";
            stm.setString(1, filtroLike);
            stm.setString(2, filtroLike);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs));
            }
            return resultado;
        } catch (SQLException ex) {
            throw new Exception("Error al buscar farmaceutas: " + ex.getMessage());
        }
    }

    // --- Método de Mapeo ---

    /**
     * Convierte una fila de ResultSet (de la tabla Usuario) a un objeto Farmaceuta.
     * @param rs El ResultSet posicionado en la fila deseada.
     * @return El objeto Farmaceuta correspondiente.
     * @throws SQLException Si hay un error al leer las columnas.
     */
    private Farmaceuta from(ResultSet rs) throws SQLException {
        Farmaceuta far = new Farmaceuta();
        far.setId(rs.getString("id"));
        far.setClave(rs.getString("clave"));
        far.setNombre(rs.getString("nombre"));
        far.setTipo(rs.getString("tipo")); // Debe ser 'Farmaceuta'
        return far;
    }
}