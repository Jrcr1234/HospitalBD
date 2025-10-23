package hospital.backend.server;

import hospital.backend.logic.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger; // Importar AtomicInteger

public class Server {
    private static final int PORT = 9090; // Puerto en el que el servidor escuchará
    // --- NUEVO: Contador atómico para clientes ---
    // AtomicInteger es seguro para usar con hilos
    private static final AtomicInteger activeClients = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            Service service = Service.getInstance();
            ServerSocket listener = new ServerSocket(PORT);
            System.out.println(">>> Servidor escuchando en el puerto " + PORT + "...");

            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println(">>> Cliente conectado desde " + clientSocket.getInetAddress().getHostAddress());

                // --- NUEVO: Incrementar contador ---
                int currentClients = activeClients.incrementAndGet();
                System.out.println(">>> Worker iniciado. Clientes activos: " + currentClients);

                // Pasamos el contador al Worker para que pueda decrementarlo al salir
                Worker worker = new Worker(clientSocket, service, activeClients);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}