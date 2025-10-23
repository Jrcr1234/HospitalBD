package hospital.frontend.client; // Paquete correcto en el frontend

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException; // Para manejar error de host desconocido

/**
 * Gestiona la conexión y comunicación Sincrónica con el servidor.
 * Encapsula los detalles del Socket y los flujos de objetos.
 */
public class Client {
    private final String serverAddress; // Dirección IP o nombre del servidor
    private final int serverPort;       // Puerto del servidor
    private Socket socket;              // Socket para la comunicación
    private ObjectInputStream input;    // Flujo para recibir objetos
    private ObjectOutputStream output;   // Flujo para enviar objetos

    /**
     * Constructor que establece la conexión Sincrónica con el servidor.
     * @param serverAddress Dirección IP o nombre del servidor.
     * @param serverPort Puerto en el que el servidor está escuchando.
     * @throws IOException Si ocurre un error de red durante la conexión.
     * @throws UnknownHostException Si la dirección del servidor no es válida.
     */
    // --- CONSTRUCTOR ---
    public Client(String serverAddress, int serverPort) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        try {
            // Establecer la conexión al crear el objeto Cliente
            this.socket = new Socket(serverAddress, serverPort);

            // IMPORTANTE: Crear Output primero asegura que el header se envíe antes de esperar datos.
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Cliente conectado al servidor en " + serverAddress + ":" + serverPort);

        } catch (UnknownHostException e) {
            System.err.println("Error: Host desconocido - " + serverAddress);
            throw e; // Relanzar para que el Service lo maneje
        } catch (IOException e) {
            System.err.println("Error de E/S al conectar con " + serverAddress + ":" + serverPort + " - " + e.getMessage());
            throw e; // Relanzar para que el Service lo maneje
        }
    }
    // --- FIN CONSTRUCTOR ---


    /**
     * Envía un objeto (petición o parámetro) al servidor.
     * @param request El objeto a enviar. Debe ser Serializable.
     * @throws IOException Si ocurre un error de red durante el envío.
     */
    public void sendRequest(Object request) throws IOException {
        if (output == null) {
            throw new IOException("La conexión no está establecida. No se puede enviar la petición.");
        }
        try {
            output.writeObject(request);
            output.flush(); // Asegura que los datos se envíen inmediatamente
        } catch (IOException e) {
            System.err.println("Error al enviar petición: " + e.getMessage());
            close(); // Intenta cerrar la conexión si falla el envío
            throw e;
        }
    }

    /**
     * Espera y recibe un objeto (respuesta) del servidor.
     * @return El objeto recibido del servidor.
     * @throws IOException Si ocurre un error de red durante la recepción.
     * @throws ClassNotFoundException Si la clase del objeto recibido no se encuentra.
     */
    public Object receiveResponse() throws IOException, ClassNotFoundException {
        if (input == null) {
            throw new IOException("La conexión no está establecida. No se puede recibir respuesta.");
        }
        try {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al recibir respuesta: " + e.getMessage());
            close(); // Intenta cerrar la conexión si falla la recepción
            throw e;
        }
    }

    /**
     * Cierra la conexión (socket y flujos) de forma segura.
     * Es importante llamar a este método cuando la aplicación se cierra.
     */
    public void close() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Conexión con el servidor cerrada.");
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        } finally {
            // Liberar referencias
            input = null;
            output = null;
            socket = null;
        }
    }

    // --- Getters (Opcionales, por si se necesita la info de conexión) ---
    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }
}