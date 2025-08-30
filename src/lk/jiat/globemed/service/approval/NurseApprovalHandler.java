package lk.jiat.globemed.service.approval;

public class NurseApprovalHandler extends ApprovalHandler {

    private static final double MAX_APPROVAL_AMOUNT = 100.0;

    public NurseApprovalHandler(String nurseName) {
        super(nurseName, "Nurse");
    }

    @Override
    protected boolean canHandle(ApprovalRequest request) {

        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
            case "PRESCRIPTION":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT
                        && !"HIGH".equals(request.getPriority())
                        && !"URGENT".equals(request.getPriority());
            case "BASIC_PROCEDURE":
                return request.getAmount() <= MAX_APPROVAL_AMOUNT;
            default:
                return false;
        }
    }

    @Override
    protected boolean approve(ApprovalRequest request) {

        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
                return approveAppointment(request);
            case "PRESCRIPTION":
                return approvePrescription(request);
            case "BASIC_PROCEDURE":
                return approveBasicProcedure(request);
            default:
                return false;
        }
    }

    private boolean approveAppointment(ApprovalRequest request) {

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Amount exceeds nurse approval limit ($" + MAX_APPROVAL_AMOUNT + ")");
            return false;
        }

        String description = request.getDescription().toLowerCase();
        if (description.contains("routine") || description.contains("checkup") || description.contains("follow-up")) {
            return true;
        }

        if (description.contains("surgery") || description.contains("emergency")) {
            request.setRejectionReason("Complex appointments require doctor approval");
            return false;
        }

        return true;
    }

    private boolean approvePrescription(ApprovalRequest request) {

        if (request.getAmount() > MAX_APPROVAL_AMOUNT) {
            request.setRejectionReason("Prescription cost exceeds nurse approval limit");
            return false;
        }

        String description = request.getDescription().toLowerCase();

        if (description.contains("painkiller") || description.contains("antibiotic")
                || description.contains("vitamin") || description.contains("basic")) {
            return true;
        }

        if (description.contains("controlled") || description.contains("narcotic")) {
            request.setRejectionReason("Controlled substances require doctor approval");
            return false;
        }

        return true;
    }

    private boolean approveBasicProcedure(ApprovalRequest request) {

        String description = request.getDescription().toLowerCase();

        if (description.contains("blood pressure") || description.contains("temperature")
                || description.contains("weight") || description.contains("basic check")) {
            return true;
        }

        request.setRejectionReason("Procedure requires higher level approval");
        return false;
    }

    @Override
    protected void onApproval(ApprovalRequest request) {
        super.onApproval(request);

        switch (request.getType().toUpperCase()) {
            case "APPOINTMENT":
                System.out.println("Nurse scheduled appointment in system");
                break;
            case "PRESCRIPTION":
                System.out.println("Nurse authorized prescription dispensing");
                break;
            case "BASIC_PROCEDURE":
                System.out.println("Nurse approved basic procedure");
                break;
        }
    }
}
