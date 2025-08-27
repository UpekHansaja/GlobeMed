package lk.jiat.globemed.service.command;

import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.StaffService;

public class AddUserCommand implements Command {

    private final StaffService staffService;
    private final Staff staff;
    private final String actor;

    public AddUserCommand(StaffService s, Staff staff, String performedBy) {
        this.staffService = s;
        this.staff = staff;
        this.actor = performedBy;
    }

    @Override
    public void execute() {
        staffService.create(staff, actor);
    }
}
