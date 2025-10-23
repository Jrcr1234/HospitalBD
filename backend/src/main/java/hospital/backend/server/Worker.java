package hospital.backend.server;

import hospital.backend.logic.Service; // Service real del backend
import hospital.protocol.logic.*; // Importa todas las entidades (Usuario, Paciente, etc.)

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List; // Necesario para los handlers que devuelven listas
import java.util.Map;  // Necesario para los handlers del dashboard
import java.util.Date; // Necesario para los handlers del dashboard
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private Socket clientSocket;
    private Service service; // Service real
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private AtomicInteger activeClients; // Contador de clientes activos

    // Constructor que recibe el service real y el contador
    public Worker(Socket clientSocket, Service service, AtomicInteger activeClients) {
        this.clientSocket = clientSocket;
        this.service = service;
        this.activeClients = activeClients;
        try {
            // Es importante crear el Output antes que el Input para evitar bloqueos
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getName() + ": Error al inicializar flujos: " + e.getMessage());
            // Considerar cerrar el socket aquí si falla la inicialización
        }
    }

    @Override
    public void run() {
        String currentThreadName = Thread.currentThread().getName(); // Guardar nombre del hilo para logs
        try {
            System.out.println(currentThreadName + ": Atendiendo peticiones...");
            // Bucle principal: Lee la acción (String)
            while (true) {
                String action = (String) input.readObject(); // Lee la acción enviada por el Service Proxy
                System.out.println(currentThreadName + ": Acción recibida '" + action + "'");
                // Pasa la acción al método que la procesará.
                // Los parámetros se leerán DENTRO de handleAction y sus sub-métodos.
                handleAction(action);
            }
        } catch (IOException | ClassNotFoundException e) {
            // Error común cuando el cliente cierra la conexión inesperadamente
            System.out.println(currentThreadName + ": Cliente " + clientSocket.getInetAddress().getHostAddress() + " se ha desconectado o hubo un error de lectura: " + e.getMessage());
        } catch (ClassCastException cce) {
            // Error si el cliente envía algo que no es un String cuando se espera la acción
            System.err.println(currentThreadName + ": Error - Se esperaba una acción (String) pero se recibió otro tipo de objeto.");
        } finally {
            closeConnection();
            // Decrementa el contador de clientes activos al finalizar
            int currentClients = activeClients.decrementAndGet();
            System.out.println(currentThreadName + ": Worker finalizado. Clientes activos: " + currentClients);
        }
    }

    // Método principal que dirige las acciones al handler correspondiente
    private void handleAction(String action) throws IOException, ClassNotFoundException {
        String currentThreadName = Thread.currentThread().getName();
        System.out.println(currentThreadName + ": Procesando acción '" + action + "'...");
        try {
            switch (action) {
            // --- Autenticación ---
            case "autenticar": handleAutenticar(); break;
            case "cambiarClave": handleCambiarClave(); break;

            // --- Medicamentos ---
            case "createMedicamento": handleCreateMedicamento(); break;
            case "updateMedicamento": handleUpdateMedicamento(); break;
            case "deleteMedicamento": handleDeleteMedicamento(); break;
            case "readMedicamento": handleReadMedicamento(); break;
            case "getMedicamentos": handleGetMedicamentos(); break;
            case "searchMedicamentos": handleSearchMedicamentos(); break;

            // --- Pacientes ---
            case "createPaciente": handleCreatePaciente(); break;
            case "readPaciente": handleReadPaciente(); break;
            case "updatePaciente": handleUpdatePaciente(); break;
            case "deletePaciente": handleDeletePaciente(); break;
            case "getPacientes": handleGetPacientes(); break;
            case "searchPacientes": handleSearchPacientes(); break;

            // --- Médicos (pendientes) ---
            case "createMedico": handleCreateMedico(); break;
            case "readMedico": handleReadMedico(); break;
            case "updateMedico": handleUpdateMedico(); break;
            case "deleteMedico": handleDeleteMedico(); break;
            case "getMedicos": handleGetMedicos(); break;
            case "searchMedicos": handleSearchMedicos(); break;

            // --- Farmaceutas (pendientes) ---
            case "createFarmaceuta": handleCreateFarmaceuta(); break;
            case "readFarmaceuta": handleReadFarmaceuta(); break;
            case "updateFarmaceuta": handleUpdateFarmaceuta(); break;
            case "deleteFarmaceuta": handleDeleteFarmaceuta(); break;
            case "getFarmaceutas": handleGetFarmaceutas(); break;
            case "searchFarmaceutas": handleSearchFarmaceutas(); break;

            // --- Recetas (pendientes) ---
            case "createReceta": handleCreateReceta(); break;
            case "updateRecetaEstado": handleUpdateRecetaEstado(); break;
            case "searchRecetasDespacho": handleSearchRecetasDespacho(); break;
            case "findRecetasHistorico": handleFindRecetasHistorico(); break;
            case "getRecetas": handleGetRecetas(); break;

            // --- Dashboard (pendientes) ---
            case "contarRecetasPorEstado": handleContarRecetasPorEstado(); break;
            case "contarMedicamentos": handleContarMedicamentosPorMes(); break;

            // --- Acción Desconocida ---
            default:
                System.err.println(currentThreadName + ": Acción desconocida recibida: " + action);
                output.writeObject(new Exception("Acción desconocida: " + action));
                break;
        }
        System.out.println(currentThreadName + ": Acción '" + action + "' procesada.");
    } catch (Exception e) {
        System.err.println(currentThreadName + ": Error procesando acción '" + action + "': " + e.getMessage());
        e.printStackTrace(); // Imprime stacktrace para depuración en el servidor
        output.writeObject(e); // Envía la excepción al cliente
    }
}
    // =======================================================
    // ===          HANDLERS PARA CADA ACCIÓN              ===
    // =======================================================
    // Cada handler lee sus parámetros, llama al Service y envía la respuesta.

    private void handleAutenticar() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        String clave = (String) input.readObject();
        Usuario usuario = service.autenticar(id, clave);
        output.writeObject(usuario);
    }

    private void handleCambiarClave() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        String actual = (String) input.readObject();
        String nueva = (String) input.readObject();
        service.cambiarClave(id, actual, nueva);
        output.writeObject("OK"); // O un objeto específico si se necesita más info
    }

    // --- Medicamentos Handlers ---
    private void handleCreateMedicamento() throws IOException, ClassNotFoundException, Exception {
        Medicamento med = (Medicamento) input.readObject();
        service.createMedicamento(med);
        output.writeObject("OK");
    }

    private void handleUpdateMedicamento() throws IOException, ClassNotFoundException, Exception {
        Medicamento med = (Medicamento) input.readObject();
        service.updateMedicamento(med);
        output.writeObject("OK");
    }

    private void handleDeleteMedicamento() throws IOException, ClassNotFoundException, Exception {
        String codigo = (String) input.readObject();
        service.deleteMedicamento(codigo);
        output.writeObject("OK");
    }

    private void handleReadMedicamento() throws IOException, ClassNotFoundException, Exception {
        String codigo = (String) input.readObject();
        Medicamento med = service.readMedicamento(codigo);
        output.writeObject(med);
    }

    private void handleGetMedicamentos() throws IOException, Exception {
        // No recibe parámetros
        List<Medicamento> list = service.getMedicamentos();
        output.writeObject(list);
    }

    private void handleSearchMedicamentos() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Medicamento> list = service.searchMedicamentos(filtro);
        output.writeObject(list);
    }

    // --- Pacientes Handlers ---
    private void handleCreatePaciente() throws IOException, ClassNotFoundException, Exception {
        Paciente p = (Paciente) input.readObject(); // Lee el objeto Paciente enviado
        service.createPaciente(p); // Llama al Service
        output.writeObject("OK"); // Envía confirmación
    }

    private void handleReadPaciente() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject(); // Lee el ID (String) enviado
        Paciente p = service.readPaciente(id); // Llama al Service
        output.writeObject(p); // Envía el objeto Paciente (o null si no se encontró)
    }

    private void handleUpdatePaciente() throws IOException, ClassNotFoundException, Exception {
        Paciente p = (Paciente) input.readObject(); // Lee el objeto Paciente enviado
        service.updatePaciente(p); // Llama al Service
        output.writeObject("OK"); // Envía confirmación
    }

    private void handleDeletePaciente() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject(); // Lee el ID (String) enviado
        service.deletePaciente(id); // Llama al Service
        output.writeObject("OK"); // Envía confirmación
    }

    private void handleGetPacientes() throws IOException, Exception {
        // No recibe parámetros
        List<Paciente> list = service.getPacientes(); // Llama al Service
        output.writeObject(list); // Envía la lista de Pacientes
    }

    private void handleSearchPacientes() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject(); // Lee el filtro (String) enviado
        List<Paciente> list = service.searchPacientes(filtro); // Llama al Service
        output.writeObject(list); // Envía la lista de Pacientes encontrados
    }

    // --- Médicos Handlers ---
    private void handleCreateMedico() throws IOException, ClassNotFoundException, Exception {
        Medico m = (Medico) input.readObject(); // Lee el objeto Medico enviado
        service.createMedico(m); // Llama al Service
        output.writeObject("OK"); // Envía confirmación
    }

    private void handleReadMedico() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject(); // Lee el ID (String) enviado
        Medico m = service.readMedico(id); // Llama al Service
        output.writeObject(m); // Envía el objeto Medico (o null)
    }

    private void handleUpdateMedico() throws IOException, ClassNotFoundException, Exception {
        Medico m = (Medico) input.readObject(); // Lee el objeto Medico enviado
        service.updateMedico(m); // Llama al Service
        output.writeObject("OK"); // Envía confirmación
    }

    private void handleDeleteMedico() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject(); // Lee el ID (String) enviado
        service.deleteMedico(id); // Llama al Service
        output.writeObject("OK"); // Envía confirmación
    }

    private void handleGetMedicos() throws IOException, Exception {
        List<Medico> list = service.getMedicos(); // Llama al Service
        output.writeObject(list); // Envía la lista de Médicos
    }

    private void handleSearchMedicos() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject(); // Lee el filtro (String) enviado
        List<Medico> list = service.searchMedicos(filtro); // Llama al Service
        output.writeObject(list); // Envía la lista de Médicos encontrados
    }


    // --- Farmaceutas Handlers ---
    private void handleCreateFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        Farmaceuta f = (Farmaceuta) input.readObject();
        service.createFarmaceuta(f);
        output.writeObject("OK");
    }

    private void handleReadFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        Farmaceuta f = service.readFarmaceuta(id);
        output.writeObject(f);
    }

    private void handleUpdateFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        Farmaceuta f = (Farmaceuta) input.readObject();
        service.updateFarmaceuta(f);
        output.writeObject("OK");
    }

    private void handleDeleteFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        service.deleteFarmaceuta(id);
        output.writeObject("OK");
    }

    private void handleGetFarmaceutas() throws IOException, Exception {
        List<Farmaceuta> list = service.getFarmaceutas();
        output.writeObject(list);
    }

    private void handleSearchFarmaceutas() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Farmaceuta> list = service.searchFarmaceutas(filtro);
        output.writeObject(list);
    }

    // --- Recetas Handlers ---
    private void handleCreateReceta() throws IOException, ClassNotFoundException, Exception {
        Receta r = (Receta) input.readObject();
        service.createReceta(r);
        output.writeObject("OK"); // O podrías devolver el código generado si lo necesitas
    }

    private void handleUpdateRecetaEstado() throws IOException, ClassNotFoundException, Exception {
        String codigo = (String) input.readObject();
        EstadoReceta estado = (EstadoReceta) input.readObject();
        service.updateRecetaEstado(codigo, estado);
        output.writeObject("OK");
    }

    private void handleSearchRecetasDespacho() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Receta> list = service.searchRecetasDespacho(filtro);
        output.writeObject(list);
    }

    private void handleFindRecetasHistorico() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Receta> list = service.findRecetasHistorico(filtro);
        output.writeObject(list);
    }

    private void handleGetRecetas() throws IOException, Exception {
        List<Receta> list = service.getRecetas();
        output.writeObject(list);
    }

    // --- Dashboard Handlers ---
    private void handleContarRecetasPorEstado() throws IOException, Exception {
        Map<String, Integer> map = service.contarRecetasPorEstado();
        output.writeObject(map);
    }

    private void handleContarMedicamentosPorMes() throws IOException, ClassNotFoundException, Exception {
        Date desde = (Date) input.readObject();
        Date hasta = (Date) input.readObject();
        List<String> nombres = (List<String>) input.readObject();
        Map<String, Integer> map = service.contarMedicamentosPorMes(desde, hasta, nombres);
        output.writeObject(map);
    }


    // --- Cierre de Conexión ---
    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            // No hacemos printStackTrace aquí para no llenar la consola si el cliente ya cerró
            // System.err.println(Thread.currentThread().getName() + ": Error al cerrar conexión: " + e.getMessage());
        }
    }
}