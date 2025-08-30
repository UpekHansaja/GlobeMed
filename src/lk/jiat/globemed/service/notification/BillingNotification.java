package lk.jiat.globemed.service.notification;

import java.time.format.DateTimeFormatter;
import lk.jiat.globemed.model.BillingRecord;

public class BillingNotification extends Notification {

    private BillingRecord billingRecord;
    private String notificationType; // GENERATED, PAID, OVERDUE, REMINDER

    public BillingNotification(NotificationSender sender, BillingRecord billingRecord, String notificationType) {
        super(sender);
        this.billingRecord = billingRecord;
        this.notificationType = notificationType;

        switch (notificationType.toUpperCase()) {
            case "OVERDUE":
                this.priority = "HIGH";
                break;
            case "REMINDER":
                this.priority = "MEDIUM";
                break;
            case "GENERATED":
            case "PAID":
                this.priority = "LOW";
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

        String message = buildBillingMessage();
        String formattedMessage = formatMessage(message);
        sender.sendNotification(formattedMessage, recipient);
    }

    private String buildBillingMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String createdDate = billingRecord.getCreatedAt().format(formatter);

        String patientName = billingRecord.getPatient().getFullName();
        double amount = billingRecord.getAmount();
        String paymentMethod = billingRecord.getPaymentMethod();
        String status = billingRecord.getStatus();

        switch (notificationType.toUpperCase()) {
            case "GENERATED":
                return String.format("A new bill has been generated for %s. "
                        + "Amount: $%.2f | Payment Method: %s | Status: %s | "
                        + "Bill ID: %d | Date: %s",
                        patientName, amount, paymentMethod, status,
                        billingRecord.getId(), createdDate);

            case "PAID":
                return String.format("Payment received! Your bill of $%.2f has been paid successfully. "
                        + "Payment Method: %s | Bill ID: %d | Date: %s | "
                        + "Thank you for your payment.",
                        amount, paymentMethod, billingRecord.getId(), createdDate);

            case "OVERDUE":
                return String.format("OVERDUE NOTICE: Your bill of $%.2f is now overdue. "
                        + "Please make payment immediately to avoid additional charges. "
                        + "Bill ID: %d | Original Date: %s | "
                        + "Contact us at (555) 123-4567 for payment options.",
                        amount, billingRecord.getId(), createdDate);

            case "REMINDER":
                return String.format("Payment Reminder: You have an outstanding bill of $%.2f. "
                        + "Payment Method: %s | Status: %s | Bill ID: %d | "
                        + "Please make payment by the due date to avoid late fees.",
                        amount, paymentMethod, status, billingRecord.getId());

            default:
                return String.format("Billing update for %s. Amount: $%.2f | Status: %s | "
                        + "Bill ID: %d | Date: %s",
                        patientName, amount, status, billingRecord.getId(), createdDate);
        }
    }

    public BillingRecord getBillingRecord() {
        return billingRecord;
    }

    public void setBillingRecord(BillingRecord billingRecord) {
        this.billingRecord = billingRecord;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
