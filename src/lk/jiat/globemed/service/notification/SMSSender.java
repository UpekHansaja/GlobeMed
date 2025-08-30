package lk.jiat.globemed.service.notification;

public class SMSSender implements NotificationSender {

    private String apiKey;
    private String apiUrl;
    private String senderNumber;

    public SMSSender(String apiKey, String apiUrl, String senderNumber) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.senderNumber = senderNumber;
    }

    @Override
    public void sendNotification(String message, String recipient) {
        // Simulate SMS sending
        System.out.println("=== SMS NOTIFICATION ===");
        System.out.println("To: " + recipient);
        System.out.println("From: " + senderNumber);
        System.out.println("Message: " + message);
        System.out.println("API: " + apiUrl);
        System.out.println("Status: SMS sent successfully");
        System.out.println("========================");

        // test sms 
    }

    @Override
    public boolean isAvailable() {
        // Check if SMS service is available
        return apiKey != null && !apiKey.isEmpty()
                && apiUrl != null && !apiUrl.isEmpty();
    }

    @Override
    public String getSenderType() {
        return "SMS";
    }

    // Getters and setters
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }
}
