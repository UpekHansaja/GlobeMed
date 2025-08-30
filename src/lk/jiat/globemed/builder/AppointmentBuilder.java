package lk.jiat.globemed.builder;

import java.time.LocalDateTime;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;

public class AppointmentBuilder {

    private Appointment appointment;

    public AppointmentBuilder() {
        this.appointment = new Appointment();

        this.appointment.setStatus("Scheduled");
    }

    public AppointmentBuilder withPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        appointment.setPatient(patient);
        return this;
    }

    public AppointmentBuilder withDoctor(Staff doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        if (doctor.getRole() == null || !"Doctor".equalsIgnoreCase(doctor.getRole().getName())) {
            throw new IllegalArgumentException("Staff member must have Doctor role");
        }
        appointment.setDoctor(doctor);
        return this;
    }

    public AppointmentBuilder withDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment cannot be scheduled in the past");
        }
        appointment.setAppointmentDateTime(dateTime);
        return this;
    }

    public AppointmentBuilder withStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        String[] validStatuses = {"Scheduled", "Cancelled", "Completed", "Rescheduled", "In Progress", "No Show"};
        boolean isValid = false;
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException("Invalid status. Valid statuses are: Scheduled, Cancelled, Completed, Rescheduled, In Progress, No Show");
        }

        appointment.setStatus(status);
        return this;
    }

    public AppointmentBuilder withUrgency(boolean urgent) {
        if (urgent) {
            appointment.setStatus("URGENT");

            if (appointment.getAppointmentDateTime() == null) {
                appointment.setAppointmentDateTime(LocalDateTime.now().plusHours(1));
            }
        }
        return this;
    }

    public AppointmentBuilder asEmergency() {
        appointment.setStatus("EMERGENCY");

        appointment.setAppointmentDateTime(LocalDateTime.now().plusMinutes(30));
        return this;
    }

    public AppointmentBuilder asFollowUp() {
        appointment.setStatus("Follow-up");
        return this;
    }

    public AppointmentBuilder asRoutineCheckup() {
        appointment.setStatus("Routine Checkup");
        return this;
    }

    public AppointmentBuilder withId(Long id) {
        appointment.setId(id);
        return this;
    }

    public Appointment build() {
        validateAppointment();

        Appointment result = new Appointment();
        result.setId(appointment.getId());
        result.setPatient(appointment.getPatient());
        result.setDoctor(appointment.getDoctor());
        result.setAppointmentDateTime(appointment.getAppointmentDateTime());
        result.setStatus(appointment.getStatus());

        return result;
    }

    private void validateAppointment() {
        if (appointment.getPatient() == null) {
            throw new IllegalStateException("Patient is required for appointment");
        }

        if (appointment.getDoctor() == null) {
            throw new IllegalStateException("Doctor is required for appointment");
        }

        if (appointment.getAppointmentDateTime() == null) {
            throw new IllegalStateException("DateTime is required for appointment");
        }

        if (appointment.getStatus() == null || appointment.getStatus().trim().isEmpty()) {
            throw new IllegalStateException("Status is required for appointment");
        }

        validateDoctorAvailability();
        validateWorkingHours();
    }

    private void validateDoctorAvailability() {

        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();
        int hour = appointmentTime.getHour();

        if (hour < 8 || hour >= 18) {
            throw new IllegalStateException("Appointment must be scheduled during working hours (8 AM - 6 PM)");
        }
    }

    private void validateWorkingHours() {
        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();

        int dayOfWeek = appointmentTime.getDayOfWeek().getValue();
        if (dayOfWeek == 6 || dayOfWeek == 7) {

            if (!"EMERGENCY".equalsIgnoreCase(appointment.getStatus())
                    && !"URGENT".equalsIgnoreCase(appointment.getStatus())) {
                throw new IllegalStateException("Regular appointments cannot be scheduled on weekends. Use emergency appointment for urgent cases.");
            }
        }
    }

    public AppointmentBuilder reset() {
        this.appointment = new Appointment();
        this.appointment.setStatus("Scheduled");
        return this;
    }
}
