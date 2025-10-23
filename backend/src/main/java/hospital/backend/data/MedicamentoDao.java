package hospital.backend.data;

import hospital.protocol.logic.Medicamento; // Importa la entidad desde protocol
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Medicamento.
 * Encapsula todo el acceso a la tabla 'Medicamento' en la base de datos.
 */
public class MedicamentoDao {

    // Obtenemos la instancia Singleton de la base de datos para usar la conexión
    Database db = Database.getInstance();

    // --- Métodos CRUD ---

    /**
     * Inserta un nuevo medicamento en la base de datos.
     * @param med El objeto Medicamento a insertar.
     * @throws Exception Si ocurre un error de SQL o el código ya existe.
     */
    public void create(Medicamento med) throws Exception {
        String sql = "INSERT INTO Medicamento (codigo, nombre, presentacion) VALUES (?, ?, ?)";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, med.getCodigo());
            stm.setString(2, med.getNombre());
            stm.setString(3, med.getPresentacion());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Medicamento no creado, filas afectadas = 0");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al crear el medicamento: " + ex.getMessage());
        }
    }

    /**
     * Busca un medicamento por su código (llave primaria).
     * @param codigo El código del medicamento a buscar.
     * @return El objeto Medicamento si se encuentra, null si no existe.
     * @throws Exception Si ocurre un error de SQL.
     */
    public Medicamento read(String codigo) throws Exception {
        String sql = "SELECT * FROM Medicamento WHERE codigo = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, codigo);
            ResultSet rs = db.executeQuery(stm);
            if (rs.next()) {
                return from(rs); // Usa el método 'from' para crear el objeto
            } else {
                return null; // No se encontró
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar el medicamento: " + ex.getMessage());
        }
    }

    /**
     * Actualiza la información de un medicamento existente.
     * @param med El objeto Medicamento con la información actualizada (se usa el código para identificar cuál actualizar).
     * @throws Exception Si ocurre un error de SQL o el medicamento no existe.
     */
    public void update(Medicamento med) throws Exception {
        String sql = "UPDATE Medicamento SET nombre = ?, presentacion = ? WHERE codigo = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, med.getNombre());
            stm.setString(2, med.getPresentacion());
            stm.setString(3, med.getCodigo());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Medicamento no actualizado, filas afectadas = 0 (Código no existe)");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al actualizar el medicamento: " + ex.getMessage());
        }
    }

    /**
     * Elimina un medicamento de la base de datos por su código.
     * @param codigo El código del medicamento a eliminar.
     * @throws Exception Si ocurre un error de SQL o el medicamento no existe.
     */
    public void delete(String codigo) throws Exception {
        String sql = "DELETE FROM Medicamento WHERE codigo = ?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, codigo);

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Medicamento no eliminado, filas afectadas = 0 (Código no existe)");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al eliminar el medicamento: " + ex.getMessage());
        }
    }

    // --- Métodos de Búsqueda ---

    /**
     * Devuelve una lista con todos los medicamentos de la tabla.
     * @return Una lista de objetos Medicamento.
     * @throws Exception Si ocurre un error de SQL.
     */
    public List<Medicamento> findAll() throws Exception {
        List<Medicamento> resultado = new ArrayList<>();
        String sql = "SELECT * FROM Medicamento ORDER BY nombre"; // Orden alfabético
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);
            while (rs.next()) {
                resultado.add(from(rs));
            }
            return resultado;
        } catch (SQLException ex) {
            throw new Exception("Error al obtener todos los medicamentos: " + ex.getMessage());
        }
    }

    /**
     * Busca medicamentos cuyo código o nombre contenga el filtro proporcionado.
     * @param filtro El texto a buscar en código o nombre.
     * @return Una lista de objetos Medicamento que coinciden.
     * @throws Exception Si ocurre un error de SQL.
     */
    public List<Medicamento> search(String filtro) throws Exception {
        List<Medicamento> resultado = new ArrayList<>();
        // Usamos LIKE '%filtro%' para búsquedas parciales (contiene)
        // Usamos LOWER() para que la búsqueda no sea sensible a mayúsculas/minúsculas
        String sql = "SELECT * FROM Medicamento WHERE LOWER(codigo) LIKE ? OR LOWER(nombre) LIKE ? ORDER BY nombre";
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
            throw new Exception("Error al buscar medicamentos: " + ex.getMessage());
        }
    }

    // --- Método de Mapeo ---

    /**
     * Método auxiliar para convertir una fila de un ResultSet a un objeto Medicamento.
     * @param rs El ResultSet posicionado en la fila deseada.
     * @return El objeto Medicamento correspondiente.
     * @throws SQLException Si hay un error al acceder a las columnas.
     */
    private Medicamento from(ResultSet rs) throws SQLException {
        Medicamento med = new Medicamento();
        med.setCodigo(rs.getString("codigo"));
        med.setNombre(rs.getString("nombre"));
        med.setPresentacion(rs.getString("presentacion"));
        return med;
    }
}