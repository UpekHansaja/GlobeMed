package lk.jiat.globemed.service.command;

import lk.jiat.globemed.service.StaffService;

public class DeleteUserCommand implements Command {

    private final StaffService staffService;
    private final Long staffId;
    private final String actor;

    public DeleteUserCommand(StaffService s, Long id, String performedBy) {
        this.staffService = s;
        this.staffId = id;
        this.actor = performedBy;
    }

    @Override
    public void execute() {
        staffService.deleteById(staffId, actor);
    }
}
