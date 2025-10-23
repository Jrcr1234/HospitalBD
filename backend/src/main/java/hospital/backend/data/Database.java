package hospital.backend.data;

import java.sql.*;

public class Database {
    // --- Atributos ---
    private static Database instance = null; // Instancia única (Singleton)
    private Connection connection;

    // --- Constructor PRIVADO ---
    // Nadie excepto la propia clase puede crear una instancia.
    private Database() {
        try {
            // 1. URL de conexión: jdbc:mysql://[servidor]:[puerto]/[base_de_datos]
            String url = "jdbc:mysql://localhost:3306/hospital_db";
            // 2. Credenciales (esto se ajusta dependiendo de las credenciales del usuario)
            String user = "root";
            String password = "Jrcr1234@";

            // 3. Establecer la conexión
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("¡Conexión a la base de datos establecida con éxito!");

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            // En una aplicación real, aquí se manejaría el error de forma más robusta.
            this.connection = null;
        }
    }

    // --- Método de acceso a la instancia (Singleton) ---
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // --- Métodos para interactuar con la BD ---

    // Prepara una consulta SQL
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    // Ejecuta una consulta que devuelve datos (SELECT)
    public ResultSet executeQuery(PreparedStatement statement) throws SQLException {
        return statement.executeQuery();
    }

    // Ejecuta una consulta que modifica datos (INSERT, UPDATE, DELETE)
    public int executeUpdate(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }

    // Cierra la conexión
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}