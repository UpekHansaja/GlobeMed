package lk.jiat.globemed.service.approval;

import java.time.LocalDateTime;

public abstract class ApprovalHandler {

    protected ApprovalHandler nextHandler;
    protected String handlerName;
    protected String role;

    public ApprovalHandler(String handlerName, String role) {
        this.handlerName = handlerName;
        this.role = role;
    }

    public void setNext(ApprovalHandler handler) {
        this.nextHandler = handler;
    }

    public ApprovalHandler getNext() {
        return nextHandler;
    }

    public final void handleRequest(ApprovalRequest request) {
        System.out.println(String.format("[%s] Processing approval request: %s",
                handlerName, request.getType()));

        if (canHandle(request)) {
            if (approve(request)) {
                request.setApproved(true);
                request.setApprovedBy(handlerName);
                request.setApprovalTime(LocalDateTime.now());

                System.out.println(String.format("[%s] APPROVED: %s (Amount: $%.2f)",
                        handlerName, request.getDescription(), request.getAmount()));

                onApproval(request);
            } else {
                System.out.println(String.format("[%s] REJECTED: %s",
                        handlerName, request.getDescription()));
                onRejection(request);
            }
        } else if (nextHandler != null) {
            System.out.println(String.format("[%s] Forwarding to next handler: %s",
                    handlerName, nextHandler.handlerName));
            nextHandler.handleRequest(request);
        } else {
            System.out.println(String.format("[%s] No handler available for request: %s",
                    handlerName, request.getType()));
            request.setRejectionReason("No appropriate handler found");
        }
    }

    protected abstract boolean canHandle(ApprovalRequest request);

    protected abstract boolean approve(ApprovalRequest request);

    protected void onApproval(ApprovalRequest request) {

        logApproval(request);
    }

    protected void onRejection(ApprovalRequest request) {

        logRejection(request);
    }

    protected void logApproval(ApprovalRequest request) {
        System.out.println(String.format("📝 Approval logged: %s approved %s at %s",
                handlerName, request.getType(), LocalDateTime.now()));
    }

    protected void logRejection(ApprovalRequest request) {
        System.out.println(String.format("📝 Rejection logged: %s rejected %s at %s",
                handlerName, request.getType(), LocalDateTime.now()));
    }

    public String getHandlerName() {
        return handlerName;
    }

    public String getRole() {
        return role;
    }
}
