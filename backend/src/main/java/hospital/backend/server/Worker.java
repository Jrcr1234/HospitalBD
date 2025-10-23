package hospital.backend.server;

import hospital.backend.logic.Service;
import hospital.protocol.Protocol; // Importar la clase Protocol
import hospital.protocol.logic.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private Socket clientSocket;
    private Service service;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private AtomicInteger activeClients;

    // Constructor (sin cambios)
    public Worker(Socket clientSocket, Service service, AtomicInteger activeClients) {
        this.clientSocket = clientSocket;
        this.service = service;
        this.activeClients = activeClients;
        try {
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getName() + ": Error al inicializar flujos: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String currentThreadName = Thread.currentThread().getName();
        try {
            System.out.println(currentThreadName + ": Atendiendo peticiones...");
            // Bucle principal: Lee la acción (Integer)
            while (true) {
                Integer actionCode = (Integer) input.readObject();
                System.out.println(currentThreadName + ": Código de acción recibido '" + actionCode + "'");
                handleAction(actionCode); // Pasa el Integer
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(currentThreadName + ": Cliente " + clientSocket.getInetAddress().getHostAddress() + " se ha desconectado o hubo un error de lectura: " + e.getMessage());
        } catch (ClassCastException cce) {
            System.err.println(currentThreadName + ": Error - Se esperaba un código de acción (Integer) pero se recibió otro tipo de objeto.");
        } finally {
            closeConnection();
            int currentClients = activeClients.decrementAndGet();
            System.out.println(currentThreadName + ": Worker finalizado. Clientes activos: " + currentClients);
        }
    }

    // Método handleAction (Usa constantes de Protocol)
    private void handleAction(Integer actionCode) throws IOException, ClassNotFoundException {
        String currentThreadName = Thread.currentThread().getName();
        System.out.println(currentThreadName + ": Procesando código de acción '" + actionCode + "'...");
        try {
            // Usa el actionCode (Integer) y las constantes de Protocol (int)
            switch (actionCode) {
                case Protocol.LOGIN: handleAutenticar(); break;
                case Protocol.CHPASS: handleCambiarClave(); break;

                // --- Medicamentos ---
                case Protocol.MEDICAMENTO_CREATE: handleCreateMedicamento(); break;
                case Protocol.MEDICAMENTO_READ: handleReadMedicamento(); break;
                case Protocol.MEDICAMENTO_UPDATE: handleUpdateMedicamento(); break;
                case Protocol.MEDICAMENTO_DELETE: handleDeleteMedicamento(); break;
                case Protocol.MEDICAMENTO_SEARCH: handleSearchMedicamentos(); break;
                case Protocol.MEDICAMENTO_GET_ALL: handleGetMedicamentos(); break;

                // --- Pacientes ---
                case Protocol.PACIENTE_CREATE: handleCreatePaciente(); break;
                case Protocol.PACIENTE_READ: handleReadPaciente(); break;
                case Protocol.PACIENTE_UPDATE: handleUpdatePaciente(); break;
                case Protocol.PACIENTE_DELETE: handleDeletePaciente(); break;
                case Protocol.PACIENTE_SEARCH: handleSearchPacientes(); break;
                case Protocol.PACIENTE_GET_ALL: handleGetPacientes(); break;

                // --- Médicos ---
                case Protocol.MEDICO_CREATE: handleCreateMedico(); break;
                case Protocol.MEDICO_READ: handleReadMedico(); break;
                case Protocol.MEDICO_UPDATE: handleUpdateMedico(); break;
                case Protocol.MEDICO_DELETE: handleDeleteMedico(); break;
                case Protocol.MEDICO_SEARCH: handleSearchMedicos(); break;
                case Protocol.MEDICO_GET_ALL: handleGetMedicos(); break;

                // --- Farmaceutas ---
                case Protocol.FARMACEUTA_CREATE: handleCreateFarmaceuta(); break;
                case Protocol.FARMACEUTA_READ: handleReadFarmaceuta(); break;
                case Protocol.FARMACEUTA_UPDATE: handleUpdateFarmaceuta(); break;
                case Protocol.FARMACEUTA_DELETE: handleDeleteFarmaceuta(); break;
                case Protocol.FARMACEUTA_SEARCH: handleSearchFarmaceutas(); break;
                case Protocol.FARMACEUTA_GET_ALL: handleGetFarmaceutas(); break;

                // --- Recetas ---
                case Protocol.RECETA_CREATE: handleCreateReceta(); break;
                case Protocol.RECETA_UPDATE_ESTADO: handleUpdateRecetaEstado(); break;
                case Protocol.RECETA_GET_DESPACHO: handleSearchRecetasDespacho(); break;
                case Protocol.RECETA_GET_HISTORICO: handleFindRecetasHistorico(); break;
                case Protocol.RECETA_GET_ALL: handleGetRecetas(); break;

                // --- Dashboard ---
                case Protocol.DASHBOARD_GET_RECETAS_ESTADO: handleContarRecetasPorEstado(); break;
                case Protocol.DASHBOARD_GET_MEDICAMENTOS_MES: handleContarMedicamentosPorMes(); break;

                // --- Chat ---
                case Protocol.CHAT_SEND: handleSendMessage(); break;

                // --- Acción Desconocida ---
                default:
                    System.err.println(currentThreadName + ": Código de acción desconocido recibido: " + actionCode);
                    output.writeObject(new Exception("Acción desconocida: " + actionCode));
                    break;
            }
            System.out.println(currentThreadName + ": Código de acción '" + actionCode + "' procesado.");
        } catch (Exception e) {
            System.err.println(currentThreadName + ": Error procesando código de acción '" + actionCode + "': " + e.getMessage());
            e.printStackTrace();
            // Enviamos la excepción, pero asegurándonos que sea Serializable
            // Si la excepción original no lo es, enviamos una genérica.
            if (e instanceof java.io.Serializable) {
                output.writeObject(e);
            } else {
                output.writeObject(new Exception("Error interno del servidor: " + e.getMessage()));
            }
        }
    }

    // =======================================================
    // ===          HANDLERS PARA CADA ACCIÓN              ===
    // =======================================================
    // (Leen parámetros, llaman al Service y envían respuesta OK o Exception)

    private void handleAutenticar() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        String clave = (String) input.readObject();
        Usuario usuario = service.autenticar(id, clave);
        output.writeObject(usuario); // Envía Usuario o lanza Exception
    }

    private void handleCambiarClave() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        String actual = (String) input.readObject();
        String nueva = (String) input.readObject();
        service.cambiarClave(id, actual, nueva); // Lanza Exception si falla
        output.writeObject(Protocol.OK); // Envía OK si tiene éxito
    }

    // --- Medicamentos Handlers ---
    private void handleCreateMedicamento() throws IOException, ClassNotFoundException, Exception {
        Medicamento med = (Medicamento) input.readObject();
        service.createMedicamento(med);
        output.writeObject(Protocol.OK);
    }
    private void handleUpdateMedicamento() throws IOException, ClassNotFoundException, Exception {
        Medicamento med = (Medicamento) input.readObject();
        service.updateMedicamento(med);
        output.writeObject(Protocol.OK);
    }
    private void handleDeleteMedicamento() throws IOException, ClassNotFoundException, Exception {
        String codigo = (String) input.readObject();
        service.deleteMedicamento(codigo);
        output.writeObject(Protocol.OK);
    }
    private void handleReadMedicamento() throws IOException, ClassNotFoundException, Exception {
        String codigo = (String) input.readObject();
        Medicamento med = service.readMedicamento(codigo);
        output.writeObject(med); // Envía Medicamento o lanza Exception
    }
    private void handleGetMedicamentos() throws IOException, Exception {
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
        Paciente p = (Paciente) input.readObject();
        service.createPaciente(p);
        output.writeObject(Protocol.OK);
    }
    private void handleReadPaciente() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        Paciente p = service.readPaciente(id);
        output.writeObject(p);
    }
    private void handleUpdatePaciente() throws IOException, ClassNotFoundException, Exception {
        Paciente p = (Paciente) input.readObject();
        service.updatePaciente(p);
        output.writeObject(Protocol.OK);
    }
    private void handleDeletePaciente() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        service.deletePaciente(id);
        output.writeObject(Protocol.OK);
    }
    private void handleGetPacientes() throws IOException, Exception {
        List<Paciente> list = service.getPacientes();
        output.writeObject(list);
    }
    private void handleSearchPacientes() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Paciente> list = service.searchPacientes(filtro);
        output.writeObject(list);
    }

    // --- Médicos Handlers ---
    private void handleCreateMedico() throws IOException, ClassNotFoundException, Exception {
        Medico m = (Medico) input.readObject();
        service.createMedico(m);
        output.writeObject(Protocol.OK);
    }
    private void handleReadMedico() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        Medico m = service.readMedico(id);
        output.writeObject(m);
    }
    private void handleUpdateMedico() throws IOException, ClassNotFoundException, Exception {
        Medico m = (Medico) input.readObject();
        service.updateMedico(m);
        output.writeObject(Protocol.OK);
    }
    private void handleDeleteMedico() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        service.deleteMedico(id);
        output.writeObject(Protocol.OK);
    }
    private void handleGetMedicos() throws IOException, Exception {
        List<Medico> list = service.getMedicos();
        output.writeObject(list);
    }
    private void handleSearchMedicos() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Medico> list = service.searchMedicos(filtro);
        output.writeObject(list);
    }


    // --- Farmaceutas Handlers ---
    private void handleCreateFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        Farmaceuta f = (Farmaceuta) input.readObject();
        service.createFarmaceuta(f);
        output.writeObject(Protocol.OK);
    }
    private void handleReadFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        Farmaceuta f = service.readFarmaceuta(id);
        output.writeObject(f);
    }
    private void handleUpdateFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        Farmaceuta f = (Farmaceuta) input.readObject();
        service.updateFarmaceuta(f);
        output.writeObject(Protocol.OK);
    }
    private void handleDeleteFarmaceuta() throws IOException, ClassNotFoundException, Exception {
        String id = (String) input.readObject();
        service.deleteFarmaceuta(id);
        output.writeObject(Protocol.OK);
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

        // CAMBIO: Capturar la receta creada que devuelve el service
        Receta recetaCreada = service.createReceta(r);

        // CAMBIO: Enviar el objeto 'Receta' en lugar de 'Protocol.OK'
        output.writeObject(recetaCreada);
    }
    private void handleUpdateRecetaEstado() throws IOException, ClassNotFoundException, Exception {
        String codigo = (String) input.readObject();
        EstadoReceta estado = (EstadoReceta) input.readObject();
        service.updateRecetaEstado(codigo, estado); // Aún no implementado en Service
        output.writeObject(Protocol.OK);
    }
    private void handleSearchRecetasDespacho() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Receta> list = service.searchRecetasDespacho(filtro); // Aún no implementado en Service
        output.writeObject(list);
    }
    private void handleFindRecetasHistorico() throws IOException, ClassNotFoundException, Exception {
        String filtro = (String) input.readObject();
        List<Receta> list = service.findRecetasHistorico(filtro); // Aún no implementado en Service
        output.writeObject(list);
    }
    private void handleGetRecetas() throws IOException, Exception {
        List<Receta> list = service.getRecetas(); // Aún no implementado en Service
        output.writeObject(list);
    }

    // --- Dashboard Handlers ---
    private void handleContarRecetasPorEstado() throws IOException, Exception {
        Map<String, Integer> map = service.contarRecetasPorEstado(); // Aún no implementado en Service
        output.writeObject(map);
    }
    private void handleContarMedicamentosPorMes() throws IOException, ClassNotFoundException, Exception {
        Date desde = (Date) input.readObject();
        Date hasta = (Date) input.readObject();
        List<String> nombres = (List<String>) input.readObject();
        Map<String, Integer> map = service.contarMedicamentosPorMes(desde, hasta, nombres); // Aún no implementado en Service
        output.writeObject(map);
    }

    // --- Chat Handler ---
    private void handleSendMessage() throws IOException, ClassNotFoundException, Exception {
        String recipientId = (String) input.readObject();
        String message = (String) input.readObject();
        // TODO: Llamar a una lógica en el Server o Service para enviar el mensaje
        System.out.println("Mensaje recibido para " + recipientId + ": " + message);
        // output.writeObject(Protocol.OK); // Podrías confirmar si quieres
        throw new UnsupportedOperationException("handleSendMessage aún no implementado"); // Temporal
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
            // Ignorar errores al cerrar
        }
    }
}