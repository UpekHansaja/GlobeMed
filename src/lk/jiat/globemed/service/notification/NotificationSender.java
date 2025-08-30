package lk.jiat.globemed.service.notification;

public interface NotificationSender {

    void sendNotification(String message, String recipient);

    boolean isAvailable();

    String getSenderType();
}
