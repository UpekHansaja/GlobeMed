package lk.jiat.globemed.service.approval;

/**
 * Concrete Handler for Chain of Responsibility Pattern
 * Handles medium-level approvals that doctors can authorize
 */
public class DoctorApprovalHandler extends ApprovalHandler {
    
    private static final double MAX_APPROVAL_AMOUNT = 5000.0;
    
    public DoctorApprovalHandler(String doctorName) {
        super(doctorName, "Doctor");
    }
    
    @Override
    protected boolean canHandle(ApprovalRequest request) {
        // Doctors can handle most medical decisions up to a certain amount
        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
            case "PRESCRIPTION":
            case "BASIC_PROCEDURE":
            case "SURGERY":
            case "TREATMENT":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
            case "STAFF_CHANGE":
                return false; // Only admin can handle staff changes
            case "BILLING":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
            default:
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
        }
    }
    
    @Override
    protected boolean approve(ApprovalRequest request) {
        // Doctor approval logic
        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
                return approveAppointment(request);
            case "PRESCRIPTION":
                return approvePrescription(request);
            case "SURGERY":
                return approveSurgery(request);
            case "TREATMENT":
                return approveTreatment(request);
            case "BILLING":
                return approveBilling(request);
            default:
                return approveGeneral(request);
        }
    }
    
    private boolean approveAppointment(ApprovalRequest request) {
        // Doctors can approve all types of appointments within their limit
        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Amount exceeds doctor approval limit ($" + MAX_APPROVAL_AMOUNT + ")");
            return false;
        }
        
        // Check priority
        if ("URGENT".equals(request.getPriority())) {
            // Urgent appointments are always approved by doctors
            return true;
        }
        
        return true; // Doctors can approve all appointments within limit
    }
    
    private boolean approvePrescription(ApprovalRequest request) {
        // Doctors can approve all prescriptions within their limit
        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Prescription cost exceeds doctor approval limit");
            return false;
        }
        
        String description = request.getDescription().toLowerCase();
        
        // Approve all medical prescriptions
        if (description.contains("medication") || description.contains("prescription") || 
            description.contains("drug") || description.contains("treatment")) {
            return true;
        }
        
        // Special handling for controlled substances
        if (description.contains("controlled") || description.contains("narcotic")) {
            if (request.getAmount() > 1000.0) {
                request.setRejectionReason("High-cost controlled substances require admin approval");
                return false;
            }
            return true; // Doctors can approve controlled substances within limit
        }
        
        return true;
    }
    
    private boolean approveSurgery(ApprovalRequest request) {
        // Surgery approval logic
        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Surgery cost exceeds doctor approval limit");
            return false;
        }
        
        String description = request.getDescription().toLowerCase();
        
        // Approve minor surgeries
        if (description.contains("minor") || description.contains("outpatient") || 
            description.contains("day surgery")) {
            return true;
        }
        
        // Major surgeries need additional validation
        if (description.contains("major") || description.contains("complex")) {
            if (request.getAmount() > 2000.0) {
                request.setRejectionReason("Major surgery over $2000 requires admin approval");
                return false;
            }
            return true;
        }
        
        return true;
    }
    
    private boolean approveTreatment(ApprovalRequest request) {
        // Treatment approval logic
        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Treatment cost exceeds doctor approval limit");
            return false;
        }
        
        // Doctors can approve all treatments within their limit
        return true;
    }
    
    private boolean approveBilling(ApprovalRequest request) {
        // Billing approval logic
        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Billing amount exceeds doctor approval limit");
            return false;
        }
        
        // Check if billing is reasonable
        if (request.getAmount() < 0) {
            request.setRejectionReason("Invalid billing amount");
            return false;
        }
        
        return true;
    }
    
    private boolean approveGeneral(ApprovalRequest request) {
        // General approval logic for other types
        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Amount exceeds doctor approval limit");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onApproval(ApprovalRequest request) {
        super.onApproval(request);
        
        // Additional doctor-specific approval actions
        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
                System.out.println("👨‍⚕️ Doctor confirmed appointment and treatment plan");
                break;
            case "PRESCRIPTION":
                System.out.println("👨‍⚕️ Doctor authorized prescription and dosage");
                break;
            case "SURGERY":
                System.out.println("👨‍⚕️ Doctor approved surgical procedure");
                break;
            case "TREATMENT":
                System.out.println("👨‍⚕️ Doctor approved treatment protocol");
                break;
            case "BILLING":
                System.out.println("👨‍⚕️ Doctor validated billing charges");
                break;
        }
    }
}