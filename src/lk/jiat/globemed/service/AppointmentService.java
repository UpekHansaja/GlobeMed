package lk.jiat.globemed.service;

import java.time.LocalDateTime;
import java.util.List;
import lk.jiat.globemed.dao.AppointmentDao;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Staff;

/**
 * Business service for appointments (thin layer above AppointmentDao). Command
 * objects will call this service to perform actions.
 */
public class AppointmentService {

    private final AppointmentDao appointmentDao = new AppointmentDao();

    public Appointment createAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("appointment is null");
        }
        // default status if not set
        if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
            appointment.setStatus("Scheduled");
        }
        return appointmentDao.create(appointment);
    }

    public Appointment findById(Long id) {
        return appointmentDao.findById(id);
    }

    public List<Appointment> findAll() {
        return appointmentDao.findAll();
    }

    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentDao.findByPatientId(patientId);
    }

    public List<Appointment> findByStaffId(Long staffId) {
        return appointmentDao.findByStaffId(staffId);
    }

    public Appointment cancelAppointment(Long appointmentId, String reason) {
        Appointment ap = appointmentDao.findById(appointmentId);
        if (ap == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }
        ap.setStatus("Cancelled");
        // optionally record reason somewhere (audit log or appointment.notes if present)
        return appointmentDao.update(ap);
    }

    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        Appointment ap = appointmentDao.findById(appointmentId);
        if (ap == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }
        ap.setAppointmentDateTime(newDateTime);
        ap.setStatus("Rescheduled");
        return appointmentDao.update(ap);
    }

    public Appointment updateAppointment(Appointment appointment) {
        return appointmentDao.update(appointment);
    }

    public boolean deleteById(Long id) {
        return appointmentDao.deleteById(id);
    }

    public List<Appointment> getAppointmentsForDoctor(Staff doctor) {
        if (doctor == null || !"Doctor".equalsIgnoreCase(doctor.getRole().getName())) {
            throw new SecurityException("Access denied: not a doctor");
        }
        return appointmentDao.findByDoctor(doctor.getId());
    }

}
