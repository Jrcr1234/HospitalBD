package hospital.backend.data;

import hospital.protocol.logic.Medico; // Importa la entidad Medico
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para Medico.
 * Interactúa con las tablas 'Usuario' y 'MedicoDetalle'.
 */
public class MedicoDao {

    Database db = Database.getInstance();

    // --- Métodos CRUD ---

    /**
     * Inserta un nuevo Medico en las tablas Usuario y MedicoDetalle.
     * @param med El objeto Medico a insertar.
     * @throws Exception Si ocurre un error de SQL o el ID ya existe.
     */
    public void create(Medico med) throws Exception {
        // Paso 1: Insertar en la tabla Usuario
        String sqlUsuario = "INSERT INTO Usuario (id, clave, nombre, tipo) VALUES (?, ?, ?, 'Medico')";
        // Paso 2: Insertar en la tabla MedicoDetalle
        String sqlDetalle = "INSERT INTO MedicoDetalle (id_medico, especialidad) VALUES (?, ?)";

        PreparedStatement stmUsuario = null;
        PreparedStatement stmDetalle = null;
        try {
            // Preparar ambas sentencias
            stmUsuario = db.prepareStatement(sqlUsuario);
            stmUsuario.setString(1, med.getId());
            stmUsuario.setString(2, med.getClave());
            stmUsuario.setString(3, med.getNombre());

            stmDetalle = db.prepareStatement(sqlDetalle);
            stmDetalle.setString(1, med.getId());
            stmDetalle.setString(2, med.getEspecialidad());

            // Ejecutar ambas inserciones (idealmente en una transacción, pero lo simplificamos)
            int countUsuario = db.executeUpdate(stmUsuario);
            if (countUsuario == 0) {
                throw new Exception("Médico no creado en Usuario, 0 filas afectadas.");
            }
            // Solo insertamos en detalle si la inserción en Usuario fue exitosa
            int countDetalle = db.executeUpdate(stmDetalle);
            if (countDetalle == 0) {
                // Si falla aquí, deberíamos deshacer la inserción en Usuario (rollback),
                // pero por simplicidad, lanzamos la excepción.
                throw new Exception("Médico no creado en MedicoDetalle, 0 filas afectadas.");
            }

        } catch (SQLException ex) {
            // Podríamos intentar deshacer la primera inserción si la segunda falla
            throw new Exception("Error al crear el médico: " + ex.getMessage());
        } finally {
            // Es buena práctica cerrar los PreparedStatements aunque Database maneje la conexión
            if (stmUsuario != null) try { stmUsuario.close(); } catch (SQLException e) {}
            if (stmDetalle != null) try { stmDetalle.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Busca un Medico por su ID, obteniendo datos de ambas tablas.
     * @param id El ID del médico a buscar.
     * @return El objeto Medico completo si se encuentra, null si no.
     * @throws Exception Si ocurre un error de SQL.
     */
    public Medico read(String id) throws Exception {
        // Consulta JOIN para combinar datos de Usuario y MedicoDetalle
        String sql = "SELECT u.id, u.clave, u.nombre, u.tipo, md.especialidad " +
                "FROM Usuario u INNER JOIN MedicoDetalle md ON u.id = md.id_medico " +
                "WHERE u.id = ? AND u.tipo = 'Medico'"; // Aseguramos que sea médico
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                // Usa el método 'from' adaptado para leer del JOIN
                return from(rs);
            } else {
                return null; // No encontrado
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar el médico: " + ex.getMessage());
        }
    }

    /**
     * Actualiza el nombre (en Usuario) y la especialidad (en MedicoDetalle).
     * @param med El objeto Medico con la información actualizada.
     * @throws Exception Si ocurre un error de SQL o el médico no existe.
     */
    public void update(Medico med) throws Exception {
        String sqlUsuario = "UPDATE Usuario SET nombre = ? WHERE id = ? AND tipo = 'Medico'";
        String sqlDetalle = "UPDATE MedicoDetalle SET especialidad = ? WHERE id_medico = ?";
        PreparedStatement stmUsuario = null;
        PreparedStatement stmDetalle = null;
        try {
            // Actualizar nombre en Usuario
            stmUsuario = db.prepareStatement(sqlUsuario);
            stmUsuario.setString(1, med.getNombre());
            stmUsuario.setString(2, med.getId());
            int countUsuario = db.executeUpdate(stmUsuario);
            if (countUsuario == 0) {
                throw new Exception("Médico no actualizado en Usuario (ID no existe o no es Médico)");
            }

            // Actualizar especialidad en MedicoDetalle
            stmDetalle = db.prepareStatement(sqlDetalle);
            stmDetalle.setString(1, med.getEspecialidad());
            stmDetalle.setString(2, med.getId());
            int countDetalle = db.executeUpdate(stmDetalle);
            // No lanzamos error si countDetalle es 0, podría no haber cambiado la especialidad.
            // Una validación más estricta podría verificar countUsuario == countDetalle.

        } catch (SQLException ex) {
            throw new Exception("Error al actualizar el médico: " + ex.getMessage());
        } finally {
            if (stmUsuario != null) try { stmUsuario.close(); } catch (SQLException e) {}
            if (stmDetalle != null) try { stmDetalle.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Elimina un Medico (borra de Usuario, MedicoDetalle se borra por CASCADE).
     * @param id El ID del médico a eliminar.
     * @throws Exception Si ocurre un error de SQL o el ID no existe o no es Médico.
     */
    public void delete(String id) throws Exception {
        // Solo necesitamos borrar de la tabla Usuario
        // La restricción FOREIGN KEY con ON DELETE CASCADE se encarga de MedicoDetalle
        String sql = "DELETE FROM Usuario WHERE id = ? AND tipo = 'Medico'";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Médico no eliminado (ID no existe o no es Médico)");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al eliminar el médico: " + ex.getMessage());
        }
    }

    // --- Métodos de Búsqueda ---

    /**
     * Devuelve una lista con todos los Médicos (con su especialidad).
     * @return Una lista de objetos Medico.
     * @throws Exception Si ocurre un error de SQL.
     */
    public List<Medico> findAll() throws Exception {
        List<Medico> resultado = new ArrayList<>();
        // Consulta JOIN para obtener todos los médicos con su especialidad
        String sql = "SELECT u.id, u.clave, u.nombre, u.tipo, md.especialidad " +
                "FROM Usuario u INNER JOIN MedicoDetalle md ON u.id = md.id_medico " +
                "WHERE u.tipo = 'Medico' ORDER BY u.nombre";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs));
            }
            return resultado;
        } catch (SQLException ex) {
            throw new Exception("Error al obtener todos los médicos: " + ex.getMessage());
        }
    }

    /**
     * Busca Médicos por ID o nombre (con especialidad).
     * @param filtro El texto a buscar en ID o nombre.
     * @return Una lista de objetos Medico que coinciden.
     * @throws Exception Si ocurre un error de SQL.
     */
    public List<Medico> search(String filtro) throws Exception {
        List<Medico> resultado = new ArrayList<>();
        // JOIN con filtro por ID o nombre en Usuario, asegurando que sean Médicos
        String sql = "SELECT u.id, u.clave, u.nombre, u.tipo, md.especialidad " +
                "FROM Usuario u INNER JOIN MedicoDetalle md ON u.id = md.id_medico " +
                "WHERE u.tipo = 'Medico' AND (LOWER(u.id) LIKE ? OR LOWER(u.nombre) LIKE ?) " +
                "ORDER BY u.nombre";
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
            throw new Exception("Error al buscar médicos: " + ex.getMessage());
        }
    }

    // --- Método de Mapeo (Actualizado para leer del JOIN) ---

    /**
     * Convierte una fila de ResultSet (resultado del JOIN) a un objeto Medico.
     * @param rs El ResultSet posicionado en la fila deseada.
     * @return El objeto Medico correspondiente.
     * @throws SQLException Si hay un error al leer las columnas.
     */
    private Medico from(ResultSet rs) throws SQLException {
        Medico med = new Medico();
        med.setId(rs.getString("id"));         // De tabla Usuario (alias u)
        med.setClave(rs.getString("clave"));   // De tabla Usuario (alias u)
        med.setNombre(rs.getString("nombre")); // De tabla Usuario (alias u)
        med.setTipo(rs.getString("tipo"));     // De tabla Usuario (alias u)
        med.setEspecialidad(rs.getString("especialidad")); // De tabla MedicoDetalle (alias md)
        return med;
    }
}