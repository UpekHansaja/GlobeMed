package lk.jiat.globemed.service.approval;

public class ApprovalChainBuilder {

    private ApprovalHandler firstHandler;
    private ApprovalHandler currentHandler;

    public ApprovalChainBuilder() {
        this.firstHandler = null;
        this.currentHandler = null;
    }

    public ApprovalChainBuilder addNurse(String nurseName) {
        NurseApprovalHandler nurse = new NurseApprovalHandler(nurseName);
        addHandler(nurse);
        return this;
    }

    public ApprovalChainBuilder addDoctor(String doctorName) {
        DoctorApprovalHandler doctor = new DoctorApprovalHandler(doctorName);
        addHandler(doctor);
        return this;
    }

    public ApprovalChainBuilder addAdmin(String adminName) {
        AdminApprovalHandler admin = new AdminApprovalHandler(adminName);
        addHandler(admin);
        return this;
    }

    public ApprovalChainBuilder addCustomHandler(ApprovalHandler handler) {
        addHandler(handler);
        return this;
    }

    private void addHandler(ApprovalHandler handler) {
        if (firstHandler == null) {
            firstHandler = handler;
            currentHandler = handler;
        } else {
            currentHandler.setNext(handler);
            currentHandler = handler;
        }
    }

    public ApprovalHandler build() {
        return firstHandler;
    }

    public static ApprovalHandler createStandardChain() {
        return new ApprovalChainBuilder()
                .addNurse("Head Nurse")
                .addDoctor("Chief Doctor")
                .addAdmin("Hospital Administrator")
                .build();
    }

    public static ApprovalHandler createEmergencyChain() {
        return new ApprovalChainBuilder()
                .addDoctor("Emergency Doctor")
                .addAdmin("Emergency Administrator")
                .build();
    }

    public static ApprovalHandler createFinancialChain() {
        return new ApprovalChainBuilder()
                .addDoctor("Attending Physician")
                .addAdmin("Financial Administrator")
                .build();
    }

    public static ApprovalHandler createAdministrativeChain() {
        return new ApprovalChainBuilder()
                .addAdmin("HR Administrator")
                .build();
    }
}
