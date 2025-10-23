package hospital.backend.data;

import hospital.protocol.logic.Paciente; // Importa la entidad desde protocol
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types; // Necesario para manejar fechas NULL
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Necesario para java.util.Date

/**
 * Data Access Object (DAO) para la entidad Paciente.
 * Encapsula todo el acceso a la tabla 'Paciente' en la base de datos.
 */
public class PacienteDao {

    Database db = Database.getInstance(); // Conexión a la BD

    // --- Métodos CRUD ---

    public void create(Paciente p) throws Exception {
        String sql = "INSERT INTO Paciente (id, nombre, fechaNacimiento, telefono) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, p.getId());
            stm.setString(2, p.getNombre());
            // Manejo especial para fechas que pueden ser null
            if (p.getFechaNacimiento() != null) {
                // Convertimos java.util.Date a java.sql.Date
                stm.setDate(3, new java.sql.Date(p.getFechaNacimiento().getTime()));
            } else {
                stm.setNull(3, Types.DATE); // Guardamos NULL si no hay fecha
            }
            stm.setString(4, p.getTelefono());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Paciente no creado, filas afectadas = 0");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al crear el paciente: " + ex.getMessage());
        }
    }

    public Paciente read(String id) throws Exception {
        String sql = "SELECT * FROM Paciente WHERE id = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return from(rs);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar el paciente: " + ex.getMessage());
        }
    }

    public void update(Paciente p) throws Exception {
        String sql = "UPDATE Paciente SET nombre = ?, fechaNacimiento = ?, telefono = ? WHERE id = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, p.getNombre());
            if (p.getFechaNacimiento() != null) {
                stm.setDate(2, new java.sql.Date(p.getFechaNacimiento().getTime()));
            } else {
                stm.setNull(2, Types.DATE);
            }
            stm.setString(3, p.getTelefono());
            stm.setString(4, p.getId());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Paciente no actualizado, ID no existe");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al actualizar el paciente: " + ex.getMessage());
        }
    }

    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Paciente WHERE id = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Paciente no eliminado, ID no existe");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al eliminar el paciente: " + ex.getMessage());
        }
    }

    // --- Métodos de Búsqueda ---

    public List<Paciente> findAll() throws Exception {
        List<Paciente> resultado = new ArrayList<>();
        String sql = "SELECT * FROM Paciente ORDER BY nombre";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs));
            }
            return resultado;
        } catch (SQLException ex) {
            throw new Exception("Error al obtener todos los pacientes: " + ex.getMessage());
        }
    }

    public List<Paciente> search(String filtro) throws Exception {
        List<Paciente> resultado = new ArrayList<>();
        String sql = "SELECT * FROM Paciente WHERE LOWER(id) LIKE ? OR LOWER(nombre) LIKE ? ORDER BY nombre";
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
            throw new Exception("Error al buscar pacientes: " + ex.getMessage());
        }
    }

    // --- Método de Mapeo ---

    private Paciente from(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getString("id"));
        p.setNombre(rs.getString("nombre"));
        // java.sql.Date se convierte automáticamente a java.util.Date al leer
        p.setFechaNacimiento(rs.getDate("fechaNacimiento"));
        p.setTelefono(rs.getString("telefono"));
        return p;
    }
}