package lk.jiat.globemed.service.approval;

public class DoctorApprovalHandler extends ApprovalHandler {

    private static final double MAX_APPROVAL_AMOUNT = 5000.0;

    public DoctorApprovalHandler(String doctorName) {
        super(doctorName, "Doctor");
    }

    @Override
    protected boolean canHandle(ApprovalRequest request) {

        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
            case "PRESCRIPTION":
            case "BASIC_PROCEDURE":
            case "SURGERY":
            case "TREATMENT":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
            case "STAFF_CHANGE":
                return false;
            case "BILLING":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
            default:
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
        }
    }

    @Override
    protected boolean approve(ApprovalRequest request) {

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

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Amount exceeds doctor approval limit ($" + MAX_APPROVAL_AMOUNT + ")");
            return false;
        }

        if ("URGENT".equals(request.getPriority())) {

            return true;
        }

        return true;
    }

    private boolean approvePrescription(ApprovalRequest request) {

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Prescription cost exceeds doctor approval limit");
            return false;
        }

        String description = request.getDescription().toLowerCase();

        if (description.contains("medication") || description.contains("prescription")
                || description.contains("drug") || description.contains("treatment")) {
            return true;
        }

        if (description.contains("controlled") || description.contains("narcotic")) {
            if (request.getAmount() > 1000.0) {
                request.setRejectionReason("High-cost controlled substances require admin approval");
                return false;
            }
            return true;
        }

        return true;
    }

    private boolean approveSurgery(ApprovalRequest request) {

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Surgery cost exceeds doctor approval limit");
            return false;
        }

        String description = request.getDescription().toLowerCase();

        if (description.contains("minor") || description.contains("outpatient")
                || description.contains("day surgery")) {
            return true;
        }

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

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Treatment cost exceeds doctor approval limit");
            return false;
        }

        return true;
    }

    private boolean approveBilling(ApprovalRequest request) {

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Billing amount exceeds doctor approval limit");
            return false;
        }

        if (request.getAmount() < 0) {
            request.setRejectionReason("Invalid billing amount");
            return false;
        }

        return true;
    }

    private boolean approveGeneral(ApprovalRequest request) {

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Amount exceeds doctor approval limit");
            return false;
        }

        return true;
    }

    @Override
    protected void onApproval(ApprovalRequest request) {
        super.onApproval(request);

        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
                System.out.println("Doctor confirmed appointment and treatment plan");
                break;
            case "PRESCRIPTION":
                System.out.println("Doctor authorized prescription and dosage");
                break;
            case "SURGERY":
                System.out.println("Doctor approved surgical procedure");
                break;
            case "TREATMENT":
                System.out.println("Doctor approved treatment protocol");
                break;
            case "BILLING":
                System.out.println("Doctor validated billing charges");
                break;
        }
    }
}
