package lk.jiat.globemed.interpreter;

import lk.jiat.globemed.model.Appointment;

public class AppointmentStatusExpression implements Expression {

    private String status;

    public AppointmentStatusExpression(String status) {
        this.status = status;
    }

    @Override
    public boolean interpret(QueryContext context) {
        Appointment appointment = (Appointment) context.getVariable("appointment");
        if (appointment == null || appointment.getStatus() == null) {
            return false;
        }

        return appointment.getStatus().equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return String.format("status = '%s'", status);
    }
}
