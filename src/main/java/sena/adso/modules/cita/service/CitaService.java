package sena.adso.modules.cita.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import sena.adso.core.exception.BusinessException;
import sena.adso.modules.cita.dao.CitaDAO;
import sena.adso.modules.cita.dao.HorarioDAO;
import sena.adso.modules.cita.model.Cita;
import sena.adso.modules.cita.model.Horario;

/**
 * Servicio de lógica de negocio para Citas.
 * 
 * Responsabilidades:
 * - Validar disponibilidad de horarios
 * - Prevenir doble-booking (race conditions)
 * - Validar reglas de negocio (citas en el pasado, etc.)
 */
public class CitaService {
    
    private final CitaDAO citaDAO;
    private final HorarioDAO horarioDAO;
    
    public CitaService() {
        this.citaDAO = new CitaDAO();
        this.horarioDAO = new HorarioDAO();
    }
    
    /**
     * Valida que un médico tenga disponibilidad para una fecha y hora.
     * 
     * @throws BusinessException si no hay disponibilidad
     */
    public void validarDisponibilidad(Integer idMedico, LocalDate fecha, LocalTime hora) {
        // 1. Verificar que la fecha no sea en el pasado
        if (fecha.isBefore(LocalDate.now())) {
            throw new BusinessException("No se pueden agendar citas en el pasado", 
                "error.cita.fecha.pasada");
        }
        
        // 2. Verificar que el médico tenga horario ese día
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1=Lunes, ..., 7=Domingo
        if (diaSemana > 5) {
            throw new BusinessException("No hay atención los fines de semana", 
                "error.cita.fin_semana");
        }
        
        List<Horario> horarios = horarioDAO.listarPorMedico(idMedico);
        boolean tieneHorario = horarios.stream()
            .anyMatch(h -> h.getDiaSemana() == diaSemana 
                && !hora.isBefore(h.getHoraInicio()) 
                && !hora.isAfter(h.getHoraFin()));
        
        if (!tieneHorario) {
            throw new BusinessException("El médico no atiende en ese horario", 
                "error.cita.horario.no_disponible");
        }
        
        // 3. Verificar que no haya conflicto con otra cita
        // Usamos franja de 1 hora por cita (ajustable)
        LocalTime horaFin = hora.plusMinutes(30);
        long citasEnFranja = citaDAO.contarCitasEnFranja(idMedico, fecha, hora, horaFin);
        
        if (citasEnFranja > 0) {
            throw new BusinessException("El médico ya tiene una cita en ese horario", 
                "error.cita.no.disponible");
        }
    }
    
    /**
     * Agenda una nueva cita con validaciones completas.
     * Debe llamarse dentro de una transacción ya iniciada.
     */
    public void agendarCita(Cita cita) {
        // Validar disponibilidad
        validarDisponibilidad(cita.getIdMedico(), cita.getFechaCita(), cita.getHoraCita());
        
        // Insertar
        citaDAO.insert(cita);
    }
    
    /**
     * Cambia el estado de una cita validando transiciones válidas.
     */
    public void cambiarEstado(Integer idCita, Cita.Estado nuevoEstado, Integer idUsuario) {
        Cita cita = citaDAO.findById(idCita)
            .orElseThrow(() -> new BusinessException("Cita no encontrada"));
        
        // Validar transiciones de estado
        boolean transicionValida = switch (cita.getEstado()) {
            case PROGRAMADA -> nuevoEstado == Cita.Estado.CONFIRMADA 
                           || nuevoEstado == Cita.Estado.CANCELADA;
            case CONFIRMADA -> nuevoEstado == Cita.Estado.ATENDIDA 
                            || nuevoEstado == Cita.Estado.CANCELADA;
            case ATENDIDA -> false; // No se puede cambiar desde ATENDIDA
            case CANCELADA -> false; // No se puede cambiar desde CANCELADA
        };
        
        if (!transicionValida) {
            throw new BusinessException("Transición de estado no válida: " 
                + cita.getEstado() + " -> " + nuevoEstado);
        }
        
        citaDAO.cambiarEstado(idCita, nuevoEstado);
    }
}