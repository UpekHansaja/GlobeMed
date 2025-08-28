package lk.jiat.globemed.service.approval;

import java.time.LocalDateTime;

/**
 * Abstract Handler for Chain of Responsibility Pattern
 * Defines the interface for handling approval requests
 */
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
    
    /**
     * Template method that handles the approval process
     */
    public final void handleRequest(ApprovalRequest request) {
        System.out.println(String.format("[%s] Processing approval request: %s", 
                                        handlerName, request.getType()));
        
        if (canHandle(request)) {
            if (approve(request)) {
                request.setApproved(true);
                request.setApprovedBy(handlerName);
                request.setApprovalTime(LocalDateTime.now());
                
                System.out.println(String.format("[%s] ✅ APPROVED: %s (Amount: $%.2f)", 
                                                handlerName, request.getDescription(), request.getAmount()));
                
                // Perform post-approval actions
                onApproval(request);
            } else {
                System.out.println(String.format("[%s] ❌ REJECTED: %s", 
                                                handlerName, request.getDescription()));
                onRejection(request);
            }
        } else if (nextHandler != null) {
            System.out.println(String.format("[%s] ⏭️ Forwarding to next handler: %s", 
                                            handlerName, nextHandler.handlerName));
            nextHandler.handleRequest(request);
        } else {
            System.out.println(String.format("[%s] ❌ No handler available for request: %s", 
                                            handlerName, request.getType()));
            request.setRejectionReason("No appropriate handler found");
        }
    }
    
    /**
     * Determines if this handler can process the request
     */
    protected abstract boolean canHandle(ApprovalRequest request);
    
    /**
     * Performs the actual approval logic
     */
    protected abstract boolean approve(ApprovalRequest request);
    
    /**
     * Called when request is approved by this handler
     */
    protected void onApproval(ApprovalRequest request) {
        // Default implementation - can be overridden
        logApproval(request);
    }
    
    /**
     * Called when request is rejected by this handler
     */
    protected void onRejection(ApprovalRequest request) {
        // Default implementation - can be overridden
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
    
    // Getters
    public String getHandlerName() {
        return handlerName;
    }
    
    public String getRole() {
        return role;
    }
}