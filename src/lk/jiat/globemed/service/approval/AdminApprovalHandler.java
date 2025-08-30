package lk.jiat.globemed.service.approval;

public class AdminApprovalHandler extends ApprovalHandler {

    private static final double MAX_APPROVAL_AMOUNT = Double.MAX_VALUE;

    public AdminApprovalHandler(String adminName) {
        super(adminName, "Administrator");
    }

    @Override
    protected boolean canHandle(ApprovalRequest request) {

        return true;
    }

    @Override
    protected boolean approve(ApprovalRequest request) {

        switch (request.getType().toUpperCase()) {
            case "STAFF_CHANGE":
                return approveStaffChange(request);
            case "BILLING":
                return approveBilling(request);
            case "SURGERY":
                return approveSurgery(request);
            case "APPOINTMENT":
                return approveAppointment(request);
            case "PRESCRIPTION":
                return approvePrescription(request);
            case "SYSTEM_CHANGE":
                return approveSystemChange(request);
            case "BUDGET":
                return approveBudget(request);
            default:
                return approveGeneral(request);
        }
    }

    private boolean approveStaffChange(ApprovalRequest request) {

        String description = request.getDescription().toLowerCase();

        if (description.contains("hire") || description.contains("fire")
                || description.contains("promote") || description.contains("role change")) {

            if (description.contains("fire") && request.getAmount() > 10000) {

                System.out.println("⚠️ High-cost termination requires board approval");
                request.setRejectionReason("High-cost termination requires board approval");
                return false;
            }

            return true;
        }

        request.setRejectionReason("Invalid staff change request");
        return false;
    }

    private boolean approveBilling(ApprovalRequest request) {

        if (request.getAmount() < 0) {
            request.setRejectionReason("Invalid billing amount");
            return false;
        }

        if (request.getAmount() > 50000) {
            System.out.println("⚠️ High-value billing flagged for audit: $" + request.getAmount());

        }

        return true;
    }

    private boolean approveSurgery(ApprovalRequest request) {

        String description = request.getDescription().toLowerCase();

        if (description.contains("experimental") || description.contains("trial")) {
            System.out.println("⚠️ Experimental procedure requires ethics committee approval");

        }

        return true;
    }

    private boolean approveAppointment(ApprovalRequest request) {

        return true;
    }

    private boolean approvePrescription(ApprovalRequest request) {

        String description = request.getDescription().toLowerCase();

        if (description.contains("controlled") || description.contains("narcotic")) {
            System.out.println("📋 Controlled substance prescription logged for DEA compliance");
        }

        return true;
    }

    private boolean approveSystemChange(ApprovalRequest request) {

        String description = request.getDescription().toLowerCase();

        if (description.contains("database") || description.contains("security")
                || description.contains("backup") || description.contains("system")) {
            return true;
        }

        request.setRejectionReason("Invalid system change request");
        return false;
    }

    private boolean approveBudget(ApprovalRequest request) {

        if (request.getAmount() < 0) {
            request.setRejectionReason("Invalid budget amount");
            return false;
        }

        if (request.getAmount() > 100000) {
            System.out.println("⚠️ Large budget change requires board notification: $" + request.getAmount());
        }

        return true;
    }

    private boolean approveGeneral(ApprovalRequest request) {

        if (request.getAmount() < 0) {
            request.setRejectionReason("Invalid amount");
            return false;
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            request.setRejectionReason("Description required for admin approval");
            return false;
        }

        return true;
    }

    @Override
    protected void onApproval(ApprovalRequest request) {
        super.onApproval(request);

        switch (request.getType().toUpperCase()) {
            case "STAFF_CHANGE":
                System.out.println("👔 Administrator processed staff change");
                notifyHR(request);
                break;
            case "BILLING":
                System.out.println("👔 Administrator approved high-value billing");
                if (request.getAmount() > 10000) {
                    notifyAccounting(request);
                }
                break;
            case "SURGERY":
                System.out.println("👔 Administrator approved surgical procedure");
                break;
            case "SYSTEM_CHANGE":
                System.out.println("👔 Administrator approved system change");
                notifyIT(request);
                break;
            case "BUDGET":
                System.out.println("👔 Administrator approved budget change");
                notifyFinance(request);
                break;
            default:
                System.out.println("👔 Administrator provided final approval");
                break;
        }
    }

    private void notifyHR(ApprovalRequest request) {
        System.out.println("📧 HR department notified of staff change approval");
    }

    private void notifyAccounting(ApprovalRequest request) {
        System.out.println("📧 Accounting department notified of high-value billing approval");
    }

    private void notifyIT(ApprovalRequest request) {
        System.out.println("📧 IT department notified of system change approval");
    }

    private void notifyFinance(ApprovalRequest request) {
        System.out.println("📧 Finance department notified of budget change approval");
    }
}
