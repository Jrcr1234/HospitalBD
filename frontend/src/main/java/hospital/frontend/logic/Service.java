package hospital.frontend.logic;

import hospital.frontend.client.Client;
import hospital.protocol.Protocol; // Importa la clase Protocol
import hospital.protocol.logic.*; // Importa todas las entidades

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service (Proxy) para el Frontend.
 * Envía códigos de operación (definidos en Protocol) al Backend.
 */
public class Service {
    // --- CÓDIGO DEL SINGLETON  ---
    private static Service theInstance;
    public static Service getInstance() {
        if (theInstance == null) {
            theInstance = new Service();
        }
        return theInstance;
    }

    // --- ATRIBUTOS ---
    private Client client;

    // --- CONSTRUCTOR (Usa puertos del Protocol) ---
    private Service() {
        try {
            this.client = new Client(Protocol.SERVER, Protocol.SYNC_PORT);
        } catch (IOException e) {
            System.err.println("CRÍTICO: No se pudo conectar al servidor. La aplicación no puede continuar.");
            e.printStackTrace();
            throw new RuntimeException("Fallo al conectar con el servidor.", e);
        }
    }

    // =======================================================
    // ===          MÉTODOS PÚBLICOS DEL SERVICIO (PROXY)  ===
    // =======================================================

    // --- MÉTODO sendRequest (Acepta int) ---
    private Object sendRequest(int actionCode, Object... params) throws Exception {
        try {
            client.sendRequest(actionCode); // Envía el código int
            for (Object param : params) {
                client.sendRequest(param);
            }
            Object response = client.receiveResponse();

            if (response instanceof Exception) {
                throw (Exception) response;
            }
            //  se puedes añadir verificación para Protocol.ERROR si se implementa en el backend
            // if (response instanceof Integer && (Integer)response == Protocol.ERROR) {
            //    throw new Exception("Error reportado por el servidor (Código: " + Protocol.ERROR + ")");
            // }
            return response;
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Error de comunicación en la acción: " + actionCode, e);
        } catch (Exception e) { // Relanzar excepciones específicas o envolver otras
            if (e instanceof Exception) throw e;
            else throw new Exception("Error inesperado durante la acción: " + actionCode, e);
        }
    }

    // --- Métodos de Autenticación y Clave ---
    public Usuario autenticar(String id, String clave) throws Exception {
        return (Usuario) sendRequest(Protocol.LOGIN, id, clave);
    }

    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        sendRequest(Protocol.CHPASS, id, claveActual, claveNueva); // No espera objeto de vuelta, solo OK o Exception
    }

    // --- MÉTODOS CRUD PARA MEDICAMENTOS ---
    public void createMedicamento(Medicamento med) throws Exception {
        sendRequest(Protocol.MEDICAMENTO_CREATE, med);
    }
    public void updateMedicamento(Medicamento med) throws Exception {
        sendRequest(Protocol.MEDICAMENTO_UPDATE, med);
    }
    public void deleteMedicamento(String codigo) throws Exception {
        sendRequest(Protocol.MEDICAMENTO_DELETE, codigo);
    }
    public Medicamento readMedicamento(String codigo) throws Exception {
        return (Medicamento) sendRequest(Protocol.MEDICAMENTO_READ, codigo);
    }
    public List<Medicamento> getMedicamentos() throws Exception {
        return (List<Medicamento>) sendRequest(Protocol.MEDICAMENTO_GET_ALL);
    }
    public List<Medicamento> searchMedicamentos(String filtro) throws Exception {
        return (List<Medicamento>) sendRequest(Protocol.MEDICAMENTO_SEARCH, filtro);
    }

    // --- MÉTODOS CRUD PARA PACIENTES ---
    public void createPaciente(Paciente p) throws Exception {
        sendRequest(Protocol.PACIENTE_CREATE, p);
    }
    public Paciente readPaciente(String id) throws Exception {
        return (Paciente) sendRequest(Protocol.PACIENTE_READ, id);
    }
    public void updatePaciente(Paciente p) throws Exception {
        sendRequest(Protocol.PACIENTE_UPDATE, p);
    }
    public void deletePaciente(String id) throws Exception {
        sendRequest(Protocol.PACIENTE_DELETE, id);
    }
    public List<Paciente> getPacientes() throws Exception {
        return (List<Paciente>) sendRequest(Protocol.PACIENTE_GET_ALL);
    }
    public List<Paciente> searchPacientes(String filtro) throws Exception {
        return (List<Paciente>) sendRequest(Protocol.PACIENTE_SEARCH, filtro);
    }

    // --- MÉTODOS CRUD PARA MÉDICOS ---
    public void createMedico(Medico m) throws Exception {
        sendRequest(Protocol.MEDICO_CREATE, m);
    }
    public Medico readMedico(String id) throws Exception {
        return (Medico) sendRequest(Protocol.MEDICO_READ, id);
    }
    public void updateMedico(Medico m) throws Exception {
        sendRequest(Protocol.MEDICO_UPDATE, m);
    }
    public void deleteMedico(String id) throws Exception {
        sendRequest(Protocol.MEDICO_DELETE, id);
    }
    public List<Medico> getMedicos() throws Exception {
        return (List<Medico>) sendRequest(Protocol.MEDICO_GET_ALL);
    }
    public List<Medico> searchMedicos(String filtro) throws Exception {
        return (List<Medico>) sendRequest(Protocol.MEDICO_SEARCH, filtro);
    }

    // --- MÉTODOS CRUD PARA FARMACEUTAS ---
    public void createFarmaceuta(Farmaceuta f) throws Exception {
        sendRequest(Protocol.FARMACEUTA_CREATE, f);
    }
    public Farmaceuta readFarmaceuta(String id) throws Exception {
        return (Farmaceuta) sendRequest(Protocol.FARMACEUTA_READ, id);
    }
    public void updateFarmaceuta(Farmaceuta f) throws Exception {
        sendRequest(Protocol.FARMACEUTA_UPDATE, f);
    }
    public void deleteFarmaceuta(String id) throws Exception {
        sendRequest(Protocol.FARMACEUTA_DELETE, id);
    }
    public List<Farmaceuta> getFarmaceutas() throws Exception {
        return (List<Farmaceuta>) sendRequest(Protocol.FARMACEUTA_GET_ALL);
    }
    public List<Farmaceuta> searchFarmaceutas(String filtro) throws Exception {
        return (List<Farmaceuta>) sendRequest(Protocol.FARMACEUTA_SEARCH, filtro);
    }

    // --- MÉTODOS PARA GESTIÓN DE RECETAS ---
    // CAMBIO 1: Cambiar 'void' por 'Receta'
    public Receta createReceta(Receta r) throws Exception {
        // CAMBIO 2: Devolver el resultado (casteado a Receta)
        return (Receta) sendRequest(Protocol.RECETA_CREATE, r);
    }
    public void updateRecetaEstado(String codigoReceta, EstadoReceta nuevoEstado) throws Exception {
        sendRequest(Protocol.RECETA_UPDATE_ESTADO, codigoReceta, nuevoEstado);
    }
    public List<Receta> searchRecetasDespacho(String filtro) throws Exception {
        return (List<Receta>) sendRequest(Protocol.RECETA_GET_DESPACHO, filtro);
    }
    public List<Receta> findRecetasHistorico(String filtro) throws Exception {
        return (List<Receta>) sendRequest(Protocol.RECETA_GET_HISTORICO, filtro);
    }
    public List<Receta> getRecetas() throws Exception {
        return (List<Receta>) sendRequest(Protocol.RECETA_GET_ALL);
    }

    // --- MÉTODOS PARA DASHBOARD ---
    public Map<String, Integer> contarRecetasPorEstado() throws Exception {
        return (Map<String, Integer>) sendRequest(Protocol.DASHBOARD_GET_RECETAS_ESTADO);
    }
    public Map<String, Integer> contarMedicamentosPorMes(Date desde, Date hasta, List<String> nombresMedicamentos) throws Exception {
        return (Map<String, Integer>) sendRequest(Protocol.DASHBOARD_GET_MEDICAMENTOS_MES, desde, hasta, nombresMedicamentos);
    }

    // --- MÉTODO PARA CHAT ---
    public void sendMessage(String recipientId, String message) throws Exception {
        sendRequest(Protocol.CHAT_SEND, recipientId, message);
    }
}