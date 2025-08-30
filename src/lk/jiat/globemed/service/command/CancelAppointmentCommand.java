package lk.jiat.globemed.service.command;

import lk.jiat.globemed.service.AppointmentService;

public class CancelAppointmentCommand implements Command {

    private final AppointmentService appointmentService;
    private final Long appointmentId;
    private final String reason;
    private final String performedBy;

    public CancelAppointmentCommand(AppointmentService appointmentService, Long appointmentId, String reason, String performedBy) {
        this.appointmentService = appointmentService;
        this.appointmentId = appointmentId;
        this.reason = reason;
        this.performedBy = performedBy;
    }

    @Override
    public void execute() {
        appointmentService.cancelAppointment(appointmentId, reason);

    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public String getReason() {
        return reason;
    }

    public String getPerformedBy() {
        return performedBy;
    }
}
