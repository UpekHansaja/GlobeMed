package lk.jiat.globemed.service.command;

import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.StaffService;

public class EditUserCommand implements Command {

    private final StaffService staffService;
    private final Staff staff;
    private final String actor;

    public EditUserCommand(StaffService s, Staff staff, String performedBy) {
        this.staffService = s;
        this.staff = staff;
        this.actor = performedBy;
    }

    @Override
    public void execute() {
        staffService.update(staff, actor);
    }
}
