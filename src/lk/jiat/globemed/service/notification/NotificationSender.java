package lk.jiat.globemed.service.notification;

/**
 * Implementor interface for Bridge Pattern
 * Defines the interface for different notification sending mechanisms
 */
public interface NotificationSender {
    void sendNotification(String message, String recipient);
    boolean isAvailable();
    String getSenderType();
}