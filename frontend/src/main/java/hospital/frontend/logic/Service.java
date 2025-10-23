package hospital.frontend.logic;

import hospital.frontend.client.Client;
import hospital.protocol.logic.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service (Proxy) para el Frontend.
 * Esta clase ya NO contiene la lógica de negocio. Actúa como un intermediario (Proxy)
 * que envía todas las peticiones al servidor real (Backend) a través de la red.
 */
public class Service {
    // --- CÓDIGO DEL SINGLETON ---
    private static Service theInstance;
    public static Service getInstance() { // Renombrado de 'instance()' a 'getInstance()' por convención
        if (theInstance == null) {
            theInstance = new Service();
        }
        return theInstance;
    }

    // --- ATRIBUTOS ---
    private Client client; // El único atributo es el cliente que se conecta al servidor.

    // --- CONSTRUCTOR ---
    // Ahora, en lugar de cargar datos XML, establece la conexión con el servidor.
    private Service() {
        try {
            this.client = new Client();
        } catch (IOException e) {
            // Si el servidor no está disponible, la aplicación no puede funcionar.
            System.err.println("CRÍTICO: No se pudo conectar al servidor. La aplicación no puede continuar.");
            e.printStackTrace();
            // Lanzamos una excepción para detener la inicialización de la app de forma controlada.
            throw new RuntimeException("Fallo al conectar con el servidor.", e);
        }
    }

    // =======================================================
    // ===          MÉTODOS PÚBLICOS DEL SERVICIO (PROXY)  ===
    // =======================================================
    // Cada método sigue el patrón:
    // 1. Enviar una "etiqueta" de la acción a realizar (nuestro protocolo).
    // 2. Enviar los parámetros necesarios.
    // 3. Recibir la respuesta del servidor.
    // 4. Procesar la respuesta: devolverla o lanzar una excepción si fue un error.
    // =======================================================

    /**
     * Envía las credenciales al servidor para la autenticación.
     */
    public Usuario autenticar(String id, String clave) throws Exception {
        try {
            client.sendRequest("autenticar");
            client.sendRequest(id);
            client.sendRequest(clave);
            Object response = client.receiveResponse();
            if (response instanceof Usuario) {
                return (Usuario) response;
            } else {
                throw (Exception) response;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Error de comunicación al autenticar.", e);
        }
    }

    /**
     * Envía la petición de cambio de clave al servidor.
     */
    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        client.sendRequest("cambiarClave");
        client.sendRequest(id);
        client.sendRequest(claveActual);
        client.sendRequest(claveNueva);
        Object response = client.receiveResponse();
        if (response instanceof Exception) {
            throw (Exception) response;
        }
    }

    // --- MÉTODOS CRUD GENÉRICOS (Abstracción para no repetir código) ---

    private Object sendRequest(String action, Object... params) throws Exception {
        try {
            client.sendRequest(action);
            for (Object param : params) {
                client.sendRequest(param);
            }
            Object response = client.receiveResponse();
            if (response instanceof Exception) {
                throw (Exception) response;
            }
            return response;
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Error de comunicación en la acción: " + action, e);
        }
    }

    // --- MÉTODOS CRUD PARA MEDICAMENTOS ---

    public void createMedicamento(Medicamento med) throws Exception {
        sendRequest("createMedicamento", med);
    }

    public void updateMedicamento(Medicamento med) throws Exception {
        sendRequest("updateMedicamento", med);
    }

    public void deleteMedicamento(String codigo) throws Exception {
        sendRequest("deleteMedicamento", codigo);
    }

    public Medicamento readMedicamento(String codigo) throws Exception {
        return (Medicamento) sendRequest("readMedicamento", codigo);
    }

    public List<Medicamento> getMedicamentos() throws Exception {
        return (List<Medicamento>) sendRequest("getMedicamentos");
    }

    public List<Medicamento> searchMedicamentos(String filtro) throws Exception {
        return (List<Medicamento>) sendRequest("searchMedicamentos", filtro);
    }

    // --- MÉTODOS CRUD PARA PACIENTES ---

    public void createPaciente(Paciente p) throws Exception {
        sendRequest("createPaciente", p);
    }

    public Paciente readPaciente(String id) throws Exception {
        return (Paciente) sendRequest("readPaciente", id);
    }

    public void updatePaciente(Paciente p) throws Exception {
        sendRequest("updatePaciente", p);
    }

    public void deletePaciente(String id) throws Exception {
        sendRequest("deletePaciente", id);
    }

    public List<Paciente> getPacientes() throws Exception {
        return (List<Paciente>) sendRequest("getPacientes");
    }

    public List<Paciente> searchPacientes(String filtro) throws Exception {
        return (List<Paciente>) sendRequest("searchPacientes", filtro);
    }

    // --- MÉTODOS CRUD PARA MÉDICOS ---

    public void createMedico(Medico m) throws Exception {
        sendRequest("createMedico", m);
    }

    public Medico readMedico(String id) throws Exception {
        return (Medico) sendRequest("readMedico", id);
    }

    public void updateMedico(Medico m) throws Exception {
        sendRequest("updateMedico", m);
    }

    public void deleteMedico(String id) throws Exception {
        sendRequest("deleteMedico", id);
    }

    public List<Medico> getMedicos() throws Exception {
        return (List<Medico>) sendRequest("getMedicos");
    }

    public List<Medico> searchMedicos(String filtro) throws Exception {
        return (List<Medico>) sendRequest("searchMedicos", filtro);
    }

    // --- MÉTODOS CRUD PARA FARMACEUTAS ---

    public void createFarmaceuta(Farmaceuta f) throws Exception {
        sendRequest("createFarmaceuta", f);
    }

    public Farmaceuta readFarmaceuta(String id) throws Exception {
        return (Farmaceuta) sendRequest("readFarmaceuta", id);
    }

    public void updateFarmaceuta(Farmaceuta f) throws Exception {
        sendRequest("updateFarmaceuta", f);
    }

    public void deleteFarmaceuta(String id) throws Exception {
        sendRequest("deleteFarmaceuta", id);
    }

    public List<Farmaceuta> getFarmaceutas() throws Exception {
        return (List<Farmaceuta>) sendRequest("getFarmaceutas");
    }

    public List<Farmaceuta> searchFarmaceutas(String filtro) throws Exception {
        return (List<Farmaceuta>) sendRequest("searchFarmaceutas", filtro);
    }

    // --- MÉTODOS PARA GESTIÓN DE RECETAS ---

    public void createReceta(Receta r) throws Exception {
        sendRequest("createReceta", r);
    }

    public void updateRecetaEstado(String codigoReceta, EstadoReceta nuevoEstado) throws Exception {
        sendRequest("updateRecetaEstado", codigoReceta, nuevoEstado);
    }

    public List<Receta> searchRecetasDespacho(String filtro) throws Exception {
        return (List<Receta>) sendRequest("searchRecetasDespacho", filtro);
    }

    public List<Receta> findRecetasHistorico(String filtro) throws Exception {
        return (List<Receta>) sendRequest("findRecetasHistorico", filtro);
    }

    public List<Receta> getRecetas() throws Exception {
        return (List<Receta>) sendRequest("getRecetas");
    }

    // --- MÉTODOS PARA DASHBOARD ---

    public Map<String, Integer> contarRecetasPorEstado() throws Exception {
        return (Map<String, Integer>) sendRequest("contarRecetasPorEstado");
    }

    public Map<String, Integer> contarMedicamentosPorMes(Date desde, Date hasta, List<String> nombresMedicamentos) throws Exception {
        // Usamos un identificador más simple para el protocolo
        return (Map<String, Integer>) sendRequest("contarMedicamentos", desde, hasta, nombresMedicamentos);
    }
}