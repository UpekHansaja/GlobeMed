package lk.jiat.globemed.service.approval;

import java.time.LocalDateTime;

public class ApprovalRequest {

    private String type; // "APPOINTMENT", "BILLING", "STAFF_CHANGE", "PRESCRIPTION", "SURGERY"
    private double amount;
    private String requestedBy;
    private String description;
    private Object data;
    private LocalDateTime requestTime;
    private String priority; // "LOW", "MEDIUM", "HIGH", "URGENT"
    private boolean approved;
    private String approvedBy;
    private LocalDateTime approvalTime;
    private String rejectionReason;

    public ApprovalRequest(String type, double amount, String requestedBy, String description, Object data) {
        this.type = type;
        this.amount = amount;
        this.requestedBy = requestedBy;
        this.description = description;
        this.data = data;
        this.requestTime = LocalDateTime.now();
        this.priority = "MEDIUM";
        this.approved = false;
    }

    public ApprovalRequest(String type, String requestedBy, String description, Object data) {
        this(type, 0.0, requestedBy, description, data);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return String.format("ApprovalRequest{type='%s', amount=%.2f, requestedBy='%s', priority='%s', approved=%s}",
                type, amount, requestedBy, priority, approved);
    }
}
