package hospital.backend.logic;

import hospital.backend.data.*; // Importa todos los DAOs
import hospital.protocol.logic.*;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
// Ya no necesitamos UUID si usamos el contador secuencial
// import java.util.UUID;

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
    private FarmaceutaDao farmaceutaDao;
    private RecetaDao recetaDao;

    // --- CONTADOR PARA CÓDIGOS DE RECETA ---
    private AtomicInteger recetaCounter; // Solo declarado aquí

    private Service() {
        // Inicializamos los DAOs que el servicio necesita para trabajar.
        this.usuarioDao = new UsuarioDao();
        this.medicamentoDao = new MedicamentoDao();
        this.pacienteDao = new PacienteDao();
        this.medicoDao = new MedicoDao();
        this.farmaceutaDao = new FarmaceutaDao();
        this.recetaDao = new RecetaDao();

        // === INICIALIZACIÓN CORRECTA DEL CONTADOR ===
        try {
            // 1. Leemos el último número de la base de datos
            int ultimoNumero = recetaDao.findMaxRecetaNumero();
            // 2. Inicializamos el contador con ese número
            this.recetaCounter = new AtomicInteger(ultimoNumero);
            System.out.println(">>> Contador de recetas inicializado en: " + ultimoNumero);
        } catch (Exception e) {
            System.err.println("CRÍTICO: No se pudo inicializar el contador de recetas desde la BD. Usando 0.");
            e.printStackTrace();
            this.recetaCounter = new AtomicInteger(0); // Plan B: empezar en 0 si falla la BD
        }
        // ===========================================
    }

    // =======================================================
    // ===          MÉTODOS DE NEGOCIO REALES              ===
    // =======================================================

    /**
     * Autentica a un usuario y devuelve el objeto COMPLETO según su rol (Medico, Farmaceuta, etc.)
     * @param id El ID del usuario.
     * @param clave La clave del usuario.
     * @return El objeto de usuario específico (Medico, Farmaceuta, o Usuario base para Admin).
     * @throws Exception Si el usuario/clave es incorrecto o el rol es inválido/no autorizado.
     */
    public Usuario autenticar(String id, String clave) throws Exception {
        // 1. Validamos la existencia y la clave en la tabla 'Usuario'
        Usuario usuarioBase = usuarioDao.read(id, clave);
        if (usuarioBase == null) {
            throw new Exception("Usuario o clave incorrectos");
        }

        // 2. Cargamos el objeto específico basado en el 'tipo'
        String tipoUsuario = usuarioBase.getTipo().trim();

        if (tipoUsuario.equalsIgnoreCase("Medico")) {
            Medico med = medicoDao.read(id);
            if (med == null) throw new Exception("Error de integridad: Usuario Médico no encontrado en detalles.");
            return med;

        } else if (tipoUsuario.equalsIgnoreCase("Farmaceuta")) {
            Farmaceuta far = farmaceutaDao.read(id);
            if (far == null) throw new Exception("Error de integridad: Usuario Farmaceuta no encontrado.");
            return far;

        } else if (tipoUsuario.equalsIgnoreCase("Administrador")) {
            return usuarioBase;

        } else {
            // Si el tipo es "Paciente" o cualquier otra cosa, no tiene permiso para loguearse.
            throw new Exception("Error: Tipo de usuario '" + usuarioBase.getTipo() + "' no está autorizado para ingresar.");
        }
    }

    /**
     * Cambia la clave de un usuario existente.
     * @param id ID del usuario.
     * @param claveActual Clave actual para verificación.
     * @param claveNueva Nueva clave a establecer.
     * @throws Exception Si la clave actual es incorrecta o hay un error de BD.
     */
    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        // 1. Verificar si el usuario y la clave actual son correctos
        Usuario usuario = usuarioDao.read(id, claveActual);
        if (usuario == null) {
            throw new Exception("Usuario o clave actual incorrectos.");
        }
        // 2. Validar la nueva clave (ej: longitud mínima, complejidad - Opcional)
        if (claveNueva == null || claveNueva.trim().length() < 4) { // Ejemplo simple
            throw new Exception("La nueva clave debe tener al menos 4 caracteres.");
        }
        // 3. Llamar al DAO para actualizar solo la contraseña.
        usuarioDao.updatePassword(id, claveNueva);
    }

    // --- MÉTODOS CRUD PARA MEDICAMENTOS ---

    public void createMedicamento(Medicamento med) throws Exception {
        // Validación básica: Código no debe existir
        Medicamento existente = medicamentoDao.read(med.getCodigo());
        if (existente != null) {
            throw new Exception("El código del medicamento ya existe.");
        }
        // Validación básica: Campos requeridos
        if (med.getCodigo() == null || med.getCodigo().trim().isEmpty() ||
                med.getNombre() == null || med.getNombre().trim().isEmpty()) {
            throw new Exception("El código y el nombre del medicamento son requeridos.");
        }
        medicamentoDao.create(med);
    }

    public void updateMedicamento(Medicamento med) throws Exception {
        // Validación básica: Campos requeridos
        if (med.getCodigo() == null || med.getCodigo().trim().isEmpty() ||
                med.getNombre() == null || med.getNombre().trim().isEmpty()) {
            throw new Exception("El código y el nombre del medicamento son requeridos.");
        }
        // El DAO se encarga de verificar si el código existe antes de actualizar
        medicamentoDao.update(med);
    }

    public void deleteMedicamento(String codigo) throws Exception {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("Se requiere el código del medicamento a eliminar.");
        }
        // El DAO se encarga de verificar si existe y maneja restricciones de FK si aplica
        medicamentoDao.delete(codigo);
    }

    public Medicamento readMedicamento(String codigo) throws Exception {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("Se requiere el código del medicamento a buscar.");
        }
        Medicamento med = medicamentoDao.read(codigo);
        if (med == null) {
            throw new Exception("Medicamento no encontrado.");
        }
        return med;
    }

    public List<Medicamento> getMedicamentos() throws Exception {
        return medicamentoDao.findAll();
    }

    public List<Medicamento> searchMedicamentos(String filtro) throws Exception {
        // Si el filtro es nulo, buscar todos (o lanzar error, según prefieras)
        return medicamentoDao.search(filtro == null ? "" : filtro);
    }

    // --- MÉTODOS CRUD PARA PACIENTES ---

    public void createPaciente(Paciente p) throws Exception {
        // Validación básica: ID no debe existir
        Paciente existente = pacienteDao.read(p.getId());
        if (existente != null) {
            throw new Exception("Ya existe un paciente con esa cédula.");
        }
        // Validación básica: Campos requeridos
        if (p.getId() == null || p.getId().trim().isEmpty() ||
                p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new Exception("La cédula y el nombre del paciente son requeridos.");
        }
        pacienteDao.create(p);
    }

    public Paciente readPaciente(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Se requiere la cédula del paciente a buscar.");
        }
        Paciente p = pacienteDao.read(id);
        if (p == null) {
            throw new Exception("Paciente no encontrado.");
        }
        return p;
    }

    public void updatePaciente(Paciente p) throws Exception {
        // Validación básica: Campos requeridos
        if (p.getId() == null || p.getId().trim().isEmpty() ||
                p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new Exception("La cédula y el nombre del paciente son requeridos.");
        }
        pacienteDao.update(p); // El DAO maneja si el ID no existe
    }

    public void deletePaciente(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Se requiere la cédula del paciente a eliminar.");
        }
        // El DAO maneja si no existe y restricciones de FK (recetas)
        pacienteDao.delete(id);
    }

    public List<Paciente> getPacientes() throws Exception {
        return pacienteDao.findAll();
    }

    public List<Paciente> searchPacientes(String filtro) throws Exception {
        return pacienteDao.search(filtro == null ? "" : filtro);
    }


    // --- MÉTODOS CRUD PARA MÉDICOS ---

    public void createMedico(Medico m) throws Exception {
        // Validación: ID no debe existir como Usuario
        Usuario existente = usuarioDao.readById(m.getId());
        if (existente != null) {
            throw new Exception("La cédula ya existe para otro usuario.");
        }
        // Validación: Campos requeridos
        if (m.getId() == null || m.getId().trim().isEmpty() ||
                m.getNombre() == null || m.getNombre().trim().isEmpty() ||
                m.getEspecialidad() == null || m.getEspecialidad().trim().isEmpty()) {
            throw new Exception("Cédula, nombre y especialidad son requeridos para el médico.");
        }
        // Asignar clave por defecto si no viene
        if (m.getClave() == null || m.getClave().isEmpty()) {
            m.setClave(m.getId()); // Clave inicial = ID
        }
        // Asignar tipo correcto
        m.setTipo("Medico");
        medicoDao.create(m);
    }

    public Medico readMedico(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Se requiere la cédula del médico a buscar.");
        }
        Medico m = medicoDao.read(id);
        if (m == null) {
            throw new Exception("Médico no encontrado.");
        }
        return m;
    }

    public void updateMedico(Medico m) throws Exception {
        // Validación: Campos requeridos
        if (m.getId() == null || m.getId().trim().isEmpty() ||
                m.getNombre() == null || m.getNombre().trim().isEmpty() ||
                m.getEspecialidad() == null || m.getEspecialidad().trim().isEmpty()) {
            throw new Exception("Cédula, nombre y especialidad son requeridos para el médico.");
        }
        // Asegurarse que el tipo no cambie (o manejarlo si se permite)
        m.setTipo("Medico");
        medicoDao.update(m); // El DAO actualiza Usuario y MedicoDetalle
    }

    public void deleteMedico(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Se requiere la cédula del médico a eliminar.");
        }
        // El DAO borra de Usuario (y MedicoDetalle por CASCADE)
        medicoDao.delete(id);
    }

    public List<Medico> getMedicos() throws Exception {
        return medicoDao.findAll();
    }

    public List<Medico> searchMedicos(String filtro) throws Exception {
        return medicoDao.search(filtro == null ? "" : filtro);
    }

    // --- MÉTODOS CRUD PARA FARMACEUTAS ---

    public void createFarmaceuta(Farmaceuta f) throws Exception {
        // Validación: ID no debe existir como Usuario
        Usuario existente = usuarioDao.readById(f.getId());
        if (existente != null) {
            throw new Exception("La cédula ya existe para otro usuario.");
        }
        // Validación: Campos requeridos
        if (f.getId() == null || f.getId().trim().isEmpty() ||
                f.getNombre() == null || f.getNombre().trim().isEmpty()) {
            throw new Exception("Cédula y nombre son requeridos para el farmaceuta.");
        }
        // Asignar clave por defecto si no viene
        if (f.getClave() == null || f.getClave().isEmpty()) {
            f.setClave(f.getId()); // Clave inicial = ID
        }
        // Asignar tipo correcto
        f.setTipo("Farmaceuta");
        farmaceutaDao.create(f);
    }

    public Farmaceuta readFarmaceuta(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Se requiere la cédula del farmaceuta a buscar.");
        }
        Farmaceuta f = farmaceutaDao.read(id);
        if (f == null) {
            throw new Exception("Farmaceuta no encontrado.");
        }
        return f;
    }

    public void updateFarmaceuta(Farmaceuta f) throws Exception {
        // Validación: Campos requeridos
        if (f.getId() == null || f.getId().trim().isEmpty() ||
                f.getNombre() == null || f.getNombre().trim().isEmpty()) {
            throw new Exception("Cédula y nombre son requeridos para el farmaceuta.");
        }
        // Asegurar tipo correcto
        f.setTipo("Farmaceuta");
        farmaceutaDao.update(f); // El DAO actualiza Usuario
    }

    public void deleteFarmaceuta(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Se requiere la cédula del farmaceuta a eliminar.");
        }
        farmaceutaDao.delete(id); // El DAO borra de Usuario
    }

    public List<Farmaceuta> getFarmaceutas() throws Exception {
        return farmaceutaDao.findAll();
    }

    public List<Farmaceuta> searchFarmaceutas(String filtro) throws Exception {
        return farmaceutaDao.search(filtro == null ? "" : filtro);
    }

    // ===========================================================
    // === MÉTODOS PARA GESTIÓN DE RECETAS ===
    // ===========================================================

    /**
     * Lógica de negocio para crear una nueva prescripción (receta).
     * Asigna código secuencial, fecha de confección y estado inicial.
     * @param r La receta a crear (viene del frontend sin código, fecha o estado).
     * @return La receta completa con código, fecha y estado asignados.
     * @throws Exception Si la validación falla o el DAO falla.
     */
    public Receta createReceta(Receta r) throws Exception {
        // 1. Validaciones de negocio
        if (r.getPaciente() == null || r.getPaciente().getId() == null) {
            throw new Exception("La receta debe tener un paciente asociado.");
        }
        if (r.getMedico() == null || r.getMedico().getId() == null) {
            throw new Exception("La receta debe tener un médico asociado.");
        }
        if (r.getLineasDetalle() == null || r.getLineasDetalle().isEmpty()) {
            throw new Exception("La receta debe tener al menos un medicamento.");
        }
        if (r.getFechaRetiro() == null) {
            throw new Exception("Se debe especificar la fecha de retiro.");
        }
        // Validar cada línea (ej: medicamento no nulo, cantidad > 0)
        for (LineaDetalle linea : r.getLineasDetalle()) {
            if (linea.getMedicamento() == null || linea.getMedicamento().getCodigo() == null) {
                throw new Exception("Cada línea debe tener un medicamento válido.");
            }
            if (linea.getCantidad() <= 0) {
                throw new Exception("La cantidad para '" + linea.getMedicamento().getNombre() + "' debe ser mayor a cero.");
            }
        }

        // 2. Lógica de negocio: Asignar valores por defecto.
        int nuevoNumero = recetaCounter.incrementAndGet(); // Incrementa desde el último leído
        String nuevoCodigo = String.format("REC-%02d", nuevoNumero);
        r.setCodigo(nuevoCodigo);
        r.setFechaConfeccion(new Date()); // Fecha actual del servidor
        r.setEstado(EstadoReceta.CONFECCIONADA); // Estado inicial
       // r.setFechaRetiro(null); // Asegurar que sea null al crear

        // Asignar IDs explícitamente si no vinieron (aunque deberían)
        r.setPacienteId(r.getPaciente().getId());
        r.setMedicoId(r.getMedico().getId());

        // 3. Llamar al DAO para persistir
        recetaDao.create(r);

        // 4. Devolver la receta con los datos asignados
        return r;
    }

    /**
     * Lógica de negocio para actualizar el estado de una receta.
     * @param codigoReceta El código de la receta.
     * @param nuevoEstado El nuevo estado a asignar.
     * @throws Exception Si el código o estado son inválidos o el DAO falla.
     */
    public void updateRecetaEstado(String codigoReceta, EstadoReceta nuevoEstado) throws Exception {
        if (codigoReceta == null || codigoReceta.trim().isEmpty()) {
            throw new Exception("El código de la receta no puede estar vacío.");
        }
        if (nuevoEstado == null) {
            throw new Exception("El nuevo estado no puede ser nulo.");
        }

        // Opcional: Podrías añadir lógica aquí para validar transiciones de estado
        // ej: if (estadoActual == ENTREGADA && nuevoEstado != ...) throw ...

        recetaDao.updateEstado(codigoReceta, nuevoEstado);
    }

    /**
     * Lógica para buscar recetas para la pantalla de Despacho (Farmaceuta).
     * @param filtro Filtro por código de receta o nombre de paciente.
     * @return Lista de recetas (información básica).
     * @throws Exception Si el DAO falla.
     */
    public List<Receta> searchRecetasDespacho(String filtro) throws Exception {
        // Podríamos añadir lógica aquí, por ejemplo, filtrar solo las 'CONFECCIONADA'
        // pero tu DAO 'searchForDespacho' ya parece hacer eso o algo similar.
        return recetaDao.searchForDespacho(filtro == null ? "" : filtro);
    }

    /**
     * Lógica para buscar recetas para la pantalla de Histórico.
     * @param filtro Filtro por código, paciente, médico o estado.
     * @return Lista de recetas (información completa con detalles).
     * @throws Exception Si el DAO falla.
     */
    public List<Receta> findRecetasHistorico(String filtro) throws Exception {
        // Devuelve recetas completas con sus líneas de detalle
        return recetaDao.searchForHistorico(filtro == null ? "" : filtro);
    }

    /**
     * Obtiene TODAS las recetas con información completa.
     * ¡Cuidado! Puede ser pesado si hay muchas recetas. Considera paginación en un futuro.
     * @return Lista de todas las recetas.
     * @throws Exception Si el DAO falla.
     */
    public List<Receta> getRecetas() throws Exception {
        return recetaDao.findAll();
    }


    // ======================================================
    // ===          MÉTODOS PARA DASHBOARD                ===
    // ======================================================

    /**
     * Lógica para obtener el conteo de recetas por estado.
     * @return Un mapa con {Estado -> Cantidad}.
     * @throws Exception Si el DAO falla.
     */
    public Map<String, Integer> contarRecetasPorEstado() throws Exception {
        return recetaDao.contarRecetasPorEstado();
    }

    /**
     * Lógica para obtener el conteo de medicamentos más usados en un rango de fechas.
     * @param desde Fecha de inicio.
     * @param hasta Fecha de fin.
     * @param nombresMedicamentos (Parámetro no usado actualmente por el DAO, pero mantenido).
     * @return Un mapa con {NombreMedicamento -> Cantidad}.
     * @throws Exception Si las fechas son inválidas o el DAO falla.
     */
    public Map<String, Integer> contarMedicamentosPorMes(Date desde, Date hasta, List<String> nombresMedicamentos) throws Exception {
        if (desde == null || hasta == null) {
            throw new Exception("Las fechas 'desde' y 'hasta' son requeridas.");
        }
        if (desde.after(hasta)) {
            throw new Exception("La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
        }
        // El DAO se encarga de la consulta SQL
        return recetaDao.contarMedicamentosPorMes(desde, hasta, nombresMedicamentos);
    }
}