package lk.jiat.globemed.service.notification;

/**
 * Abstraction class for Bridge Pattern
 * Defines the interface for different types of notifications
 */
public abstract class Notification {
    protected NotificationSender sender;
    protected String priority; // LOW, MEDIUM, HIGH, URGENT
    
    public Notification(NotificationSender sender) {
        this.sender = sender;
        this.priority = "MEDIUM";
    }
    
    public Notification(NotificationSender sender, String priority) {
        this.sender = sender;
        this.priority = priority;
    }
    
    public abstract void notify(String recipient);
    
    protected String formatMessage(String baseMessage) {
        String priorityPrefix = "";
        switch (priority.toUpperCase()) {
            case "URGENT":
                priorityPrefix = "🚨 URGENT: ";
                break;
            case "HIGH":
                priorityPrefix = "⚠️ HIGH PRIORITY: ";
                break;
            case "MEDIUM":
                priorityPrefix = "ℹ️ ";
                break;
            case "LOW":
                priorityPrefix = "📝 ";
                break;
        }
        return priorityPrefix + baseMessage;
    }
    
    public void setSender(NotificationSender sender) {
        this.sender = sender;
    }
    
    public NotificationSender getSender() {
        return sender;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
}