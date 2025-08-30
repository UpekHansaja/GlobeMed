package lk.jiat.globemed.report;

import java.util.List;
import lk.jiat.globemed.model.Staff;

public interface ReportVisitor {

    void visitStaffList(List<Staff> staffList);

}
