package lk.jiat.globemed.service.command;

import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.service.AppointmentService;

public class CreateAppointmentCommand implements Command {

    private final AppointmentService appointmentService;
    private final Appointment appointment;
    private final String performedBy;

    public CreateAppointmentCommand(AppointmentService service, Appointment appointment, String performedBy) {
        this.appointmentService = service;
        this.appointment = appointment;
        this.performedBy = performedBy;
    }

    @Override
    public void execute() {

        appointmentService.createAppointment(appointment);
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getPerformedBy() {
        return performedBy;
    }
}
