package sena.adso.modules.consulta.service;

import java.util.List;

import sena.adso.modules.cita.dao.CitaDAO;
import sena.adso.modules.cita.model.Cita;
import sena.adso.modules.paciente.dao.PacienteDAO;
import sena.adso.modules.paciente.model.Paciente;

/**
 * Servicio de consulta pública de citas médicas.
 * No requiere autenticación. Permite a pacientes consultar el estado de sus
 * citas.
 */
public class ConsultaService {

    private final PacienteDAO pacienteDAO;
    private final CitaDAO citaDAO;

    public ConsultaService(PacienteDAO pacienteDAO, CitaDAO citaDAO) {
        this.pacienteDAO = pacienteDAO;
        this.citaDAO = citaDAO;
    }

    /**
     * Busca un paciente por su número de documento.
     *
     * @param documento Número de documento del paciente
     * @return Paciente encontrado, o null si no existe
     */
    public Paciente buscarPacientePorDocumento(String documento) {
        return pacienteDAO.buscarPorDocumento(documento).orElse(null);
    }

    /**
     * Obtiene todas las citas de un paciente específico, ordenadas por fecha
     * descendente.
     *
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     */
    public List<Cita> obtenerCitasPorPaciente(Integer idPaciente) {
        return citaDAO.listarPorPaciente(idPaciente);
    }

    /**
     * Valida que un documento tenga formato correcto.
     *
     * @param documento Documento a validar
     * @return true si el formato es válido
     */
    public boolean validarDocumento(String documento) {
        return documento != null
                && !documento.trim().isEmpty()
                && documento.trim().matches("\\d{6,12}");
    }
}