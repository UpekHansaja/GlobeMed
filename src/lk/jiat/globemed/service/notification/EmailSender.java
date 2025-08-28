package lk.jiat.globemed.service.notification;

/**
 * Concrete Implementor for Bridge Pattern
 * Handles email notifications
 */
public class EmailSender implements NotificationSender {
    private String smtpServer;
    private int port;
    private String username;
    private String password;
    
    public EmailSender(String smtpServer, int port, String username, String password) {
        this.smtpServer = smtpServer;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public void sendNotification(String message, String recipient) {
        // Simulate email sending
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + recipient);
        System.out.println("From: " + username);
        System.out.println("Subject: GlobeMed Notification");
        System.out.println("Message: " + message);
        System.out.println("Sent via: " + smtpServer + ":" + port);
        System.out.println("Status: Email sent successfully");
        System.out.println("==========================");
        
        // In real implementation, you would use JavaMail API
        // Properties props = new Properties();
        // props.put("mail.smtp.host", smtpServer);
        // props.put("mail.smtp.port", port);
        // ... actual email sending logic
    }
    
    @Override
    public boolean isAvailable() {
        // Check if email service is available
        return smtpServer != null && !smtpServer.isEmpty() && port > 0;
    }
    
    @Override
    public String getSenderType() {
        return "EMAIL";
    }
    
    // Getters and setters
    public String getSmtpServer() {
        return smtpServer;
    }
    
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}