package hospital.backend.logic;

import hospital.backend.data.UsuarioDao;
import hospital.backend.data.MedicamentoDao;
import hospital.backend.data.PacienteDao;
import hospital.backend.data.MedicoDao;
import hospital.protocol.logic.*;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * Service (Real) para el Backend.
 * Esta clase CONTIENE la lógica de negocio real y accede a la capa de datos (DAOs).
 * Es el "verdadero" service que el Proxy del cliente representa.
 */
public class Service {
    // --- CÓDIGO DEL SINGLETON ---
    private static Service theInstance;
    public static Service getInstance() {
        if (theInstance == null) {
            theInstance = new Service();
        }
        return theInstance;
    }

    // --- ATRIBUTOS (LOS DAOs) ---
    private UsuarioDao usuarioDao;
    private MedicamentoDao medicamentoDao;
    private PacienteDao pacienteDao;
    private MedicoDao medicoDao;
    // Aquí irán los demás DAOs: PacienteDao, MedicamentoDao, etc.

    private Service() {
        // Inicializamos los DAOs que el servicio necesita para trabajar.
        this.usuarioDao = new UsuarioDao();
        this.medicamentoDao = new MedicamentoDao();
        this.pacienteDao = new PacienteDao();
        this.medicoDao = new MedicoDao();
    }

    // =======================================================
    // ===          MÉTODOS DE NEGOCIO REALES              ===
    // =======================================================

    public Usuario autenticar(String id, String clave) throws Exception {
        Usuario usuario = usuarioDao.read(id, clave);
        if (usuario != null) {
            return usuario;
        } else {
            throw new Exception("Usuario o clave incorrectos");
        }
    }

    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        // 1. Verificar si el usuario y la clave actual son correctos usando el método read existente.
        Usuario usuario = usuarioDao.read(id, claveActual);
        if (usuario == null) {
            // Si read devuelve null, la combinación ID/clave actual es incorrecta.
            throw new Exception("Usuario o clave actual incorrectos.");
        }

        // 2. Si son correctos, llamar al nuevo método del DAO para actualizar solo la contraseña.
        usuarioDao.updatePassword(id, claveNueva);

        // 3. (Opcional) Se podría añadir lógica adicional aquí si fuera necesario,
        //    como registrar el evento de cambio de clave en un log.
    }

    // --- MÉTODOS CRUD PARA MEDICAMENTOS (IMPLEMENTADOS) ---

    public void createMedicamento(Medicamento med) throws Exception {
        // Podríamos añadir validación aquí si el código ya existe antes de llamar al DAO
        Medicamento existente = medicamentoDao.read(med.getCodigo());
        if (existente != null) {
            throw new Exception("El código del medicamento ya existe.");
        }
        medicamentoDao.create(med); // Llama al DAO
    }

    public void updateMedicamento(Medicamento med) throws Exception {
        // El DAO ya valida si existe al intentar actualizar
        medicamentoDao.update(med); // Llama al DAO
    }

    public void deleteMedicamento(String codigo) throws Exception {
        // El DAO ya valida si existe al intentar borrar
        medicamentoDao.delete(codigo); // Llama al DAO
    }

    public Medicamento readMedicamento(String codigo) throws Exception {
        Medicamento med = medicamentoDao.read(codigo); // Llama al DAO
        if (med == null) {
            throw new Exception("Medicamento no encontrado.");
        }
        return med;
    }

    public List<Medicamento> getMedicamentos() throws Exception {
        return medicamentoDao.findAll(); // Llama al DAO
    }

    public List<Medicamento> searchMedicamentos(String filtro) throws Exception {
        return medicamentoDao.search(filtro); // Llama al DAO
    }

    // --- MÉTODOS CRUD PARA PACIENTES (IMPLEMENTADOS) ---

    public void createPaciente(Paciente p) throws Exception {
        // Validación básica: ¿Ya existe un paciente con ese ID?
        Paciente existente = pacienteDao.read(p.getId());
        if (existente != null) {
            throw new Exception("Ya existe un paciente con esa cédula.");
        }
        pacienteDao.create(p); // Llama al DAO
    }

    public Paciente readPaciente(String id) throws Exception {
        Paciente p = pacienteDao.read(id); // Llama al DAO
        if (p == null) {
            throw new Exception("Paciente no encontrado.");
        }
        return p;
    }

    public void updatePaciente(Paciente p) throws Exception {
        pacienteDao.update(p); // Llama al DAO (el DAO maneja si el ID no existe)
    }

    public void deletePaciente(String id) throws Exception {
        pacienteDao.delete(id); // Llama al DAO (el DAO maneja si el ID no existe)
    }

    public List<Paciente> getPacientes() throws Exception {
        return pacienteDao.findAll(); // Llama al DAO
    }

    public List<Paciente> searchPacientes(String filtro) throws Exception {
        return pacienteDao.search(filtro); // Llama al DAO
    }


    // --- MÉTODOS CRUD PARA MÉDICOS (IMPLEMENTADOS) ---

    public void createMedico(Medico m) throws Exception {
        // Validación: Asegurarse de que el ID no exista ya como *cualquier* tipo de usuario.
        // Podríamos hacer esto consultando UsuarioDao, pero MedicoDao.create fallará si el ID ya existe.
        // Asignamos una clave por defecto si no viene una (podría ajustarse según reglas de negocio)
        if (m.getClave() == null || m.getClave().isEmpty()) {
            m.setClave(m.getId()); // Clave inicial = ID
        }
        medicoDao.create(m); // Llama al DAO
    }

    public Medico readMedico(String id) throws Exception {
        Medico m = medicoDao.read(id); // Llama al DAO
        if (m == null) {
            throw new Exception("Médico no encontrado.");
        }
        return m;
    }

    public void updateMedico(Medico m) throws Exception {
        // El DAO se encarga de actualizar ambas tablas (Usuario y MedicoDetalle)
        medicoDao.update(m); // Llama al DAO
    }

    public void deleteMedico(String id) throws Exception {
        // El DAO se encarga de borrar de Usuario (y MedicoDetalle por CASCADE)
        medicoDao.delete(id); // Llama al DAO
    }

    public List<Medico> getMedicos() throws Exception {
        return medicoDao.findAll(); // Llama al DAO
    }

    public List<Medico> searchMedicos(String filtro) throws Exception {
        return medicoDao.search(filtro); // Llama al DAO
    }

    // --- MÉTODOS CRUD PARA FARMACEUTAS (A IMPLEMENTAR) ---
    // ... (siguen con UnsupportedOperationException) ...
    public void createFarmaceuta(Farmaceuta f) throws Exception {
        // TODO: Validar ID. Llamar a farmaceutaDao.create(f);
        throw new UnsupportedOperationException("Método createFarmaceuta no implementado.");
    }

    public Farmaceuta readFarmaceuta(String id) throws Exception {
        // TODO: return farmaceutaDao.read(id);
        throw new UnsupportedOperationException("Método readFarmaceuta no implementado.");
    }

    public void updateFarmaceuta(Farmaceuta f) throws Exception {
        // TODO: Llamar a farmaceutaDao.update(f);
        throw new UnsupportedOperationException("Método updateFarmaceuta no implementado.");
    }

    public void deleteFarmaceuta(String id) throws Exception {
        // TODO: Llamar a farmaceutaDao.delete(id);
        throw new UnsupportedOperationException("Método deleteFarmaceuta no implementado.");
    }

    public List<Farmaceuta> getFarmaceutas() throws Exception {
        // TODO: return farmaceutaDao.findAll();
        throw new UnsupportedOperationException("Método getFarmaceutas no implementado.");
    }

    public List<Farmaceuta> searchFarmaceutas(String filtro) throws Exception {
        // TODO: return farmaceutaDao.search(filtro);
        throw new UnsupportedOperationException("Método searchFarmaceutas no implementado.");
    }

    // --- MÉTODOS PARA GESTIÓN DE RECETAS (A IMPLEMENTAR) ---
    // ... (siguen con UnsupportedOperationException) ...
    public void createReceta(Receta r) throws Exception {
        // TODO: Generar código, asignar estado inicial CONFECCIONADA. Llamar a recetaDao.create(r);
        throw new UnsupportedOperationException("Método createReceta no implementado.");
    }

    public void updateRecetaEstado(String codigoReceta, EstadoReceta nuevoEstado) throws Exception {
        // TODO: Llamar a recetaDao.updateEstado(codigoReceta, nuevoEstado);
        throw new UnsupportedOperationException("Método updateRecetaEstado no implementado.");
    }

    public List<Receta> searchRecetasDespacho(String filtro) throws Exception {
        // TODO: return recetaDao.searchForDespacho(filtro);
        throw new UnsupportedOperationException("Método searchRecetasDespacho no implementado.");
    }

    public List<Receta> findRecetasHistorico(String filtro) throws Exception {
        // TODO: return recetaDao.searchForHistorico(filtro);
        throw new UnsupportedOperationException("Método findRecetasHistorico no implementado.");
    }

    public List<Receta> getRecetas() throws Exception {
        // TODO: return recetaDao.findAll();
        throw new UnsupportedOperationException("Método getRecetas no implementado.");
    }


    // --- MÉTODOS PARA DASHBOARD (A IMPLEMENTAR) ---
    // ... (siguen con UnsupportedOperationException) ...
    public Map<String, Integer> contarRecetasPorEstado() throws Exception {
        // TODO: Lógica para contar recetas por estado usando RecetaDao
        throw new UnsupportedOperationException("Método contarRecetasPorEstado no implementado.");
    }

    public Map<String, Integer> contarMedicamentosPorMes(Date desde, Date hasta, List<String> nombresMedicamentos) throws Exception {
        // TODO: Lógica para contar medicamentos por mes usando RecetaDao y LineaDetalleDao
        // ¡Recuerda devolver Map<String, Integer>!
        throw new UnsupportedOperationException("Método contarMedicamentosPorMes no implementado.");
    }

}