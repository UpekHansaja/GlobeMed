package lk.jiat.globemed.service.command;

import java.time.LocalDateTime;
import lk.jiat.globemed.service.AppointmentService;

public class RescheduleAppointmentCommand implements Command {

    private final AppointmentService appointmentService;
    private final Long appointmentId;
    private final LocalDateTime newDateTime;
    private final String performedBy;

    public RescheduleAppointmentCommand(AppointmentService appointmentService, Long appointmentId, LocalDateTime newDateTime, String performedBy) {
        this.appointmentService = appointmentService;
        this.appointmentId = appointmentId;
        this.newDateTime = newDateTime;
        this.performedBy = performedBy;
    }

    @Override
    public void execute() {
        appointmentService.rescheduleAppointment(appointmentId, newDateTime);
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public LocalDateTime getNewDateTime() {
        return newDateTime;
    }

    public String getPerformedBy() {
        return performedBy;
    }
}
