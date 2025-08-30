package lk.jiat.globemed.service.notification;

import java.time.format.DateTimeFormatter;
import lk.jiat.globemed.model.Appointment;

public class AppointmentNotification extends Notification {

    private Appointment appointment;
    private String notificationType; // SCHEDULED, CANCELLED, RESCHEDULED, REMINDER

    public AppointmentNotification(NotificationSender sender, Appointment appointment, String notificationType) {

        super(sender);
        this.appointment = appointment;
        this.notificationType = notificationType;

        switch (notificationType.toUpperCase()) {
            case "CANCELLED":
                this.priority = "HIGH";
                break;
            case "REMINDER":
                this.priority = "MEDIUM";
                break;
            case "SCHEDULED":
            case "RESCHEDULED":
                this.priority = "MEDIUM";
                break;
            default:
                this.priority = "LOW";
        }
    }

    @Override
    public void notify(String recipient) {
        if (!sender.isAvailable()) {
            System.err.println("Notification sender is not available: " + sender.getSenderType());
            return;
        }

        String message = buildAppointmentMessage();
        String formattedMessage = formatMessage(message);
        sender.sendNotification(formattedMessage, recipient);
    }

    private String buildAppointmentMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        String dateTime = appointment.getAppointmentDateTime().format(formatter);

        String patientName = appointment.getPatient().getFullName();
        String doctorName = appointment.getDoctor().getName();

        switch (notificationType.toUpperCase()) {
            case "SCHEDULED":
                return String.format("Your appointment has been scheduled for %s with Dr. %s. "
                        + "Please arrive 15 minutes early. Appointment ID: %d",
                        dateTime, doctorName, appointment.getId());

            case "CANCELLED":
                return String.format("Your appointment scheduled for %s with Dr. %s has been cancelled. "
                        + "Please contact us to reschedule. Appointment ID: %d",
                        dateTime, doctorName, appointment.getId());

            case "RESCHEDULED":
                return String.format("Your appointment has been rescheduled to %s with Dr. %s. "
                        + "Please make note of the new time. Appointment ID: %d",
                        dateTime, doctorName, appointment.getId());

            case "REMINDER":
                return String.format("Reminder: You have an appointment tomorrow at %s with Dr. %s. "
                        + "Please arrive 15 minutes early. Appointment ID: %d",
                        dateTime, doctorName, appointment.getId());

            default:
                return String.format("Appointment update for %s with Dr. %s on %s. Appointment ID: %d",
                        patientName, doctorName, dateTime, appointment.getId());
        }
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
