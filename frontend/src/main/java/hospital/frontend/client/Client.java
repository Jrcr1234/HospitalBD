package hospital.frontend.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // O la IP del servidor
    private static final int SERVER_PORT = 9090; // El mismo puerto que el servidor

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public Client() throws IOException {
        // Conectar al servidor al crear el objeto Cliente
        this.socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Cliente conectado al servidor en " + SERVER_ADDRESS + ":" + SERVER_PORT);
    }

    // Método para enviar una petición (un objeto) al servidor
    public void sendRequest(Object request) throws IOException {
        output.writeObject(request);
    }

    // Método para recibir una respuesta (un objeto) del servidor
    public Object receiveResponse() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    // Método para cerrar la conexión
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Conexión con el servidor cerrada.");
        }
    }
}