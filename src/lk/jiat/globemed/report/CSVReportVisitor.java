package lk.jiat.globemed.report;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import lk.jiat.globemed.model.Staff;

public class CSVReportVisitor implements ReportVisitor {

    private final String filepath;

    public CSVReportVisitor(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void visitStaffList(List<Staff> staffList) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filepath))) {
            out.println("id,name,email,role");
            for (Staff s : staffList) {
                String role = s.getRole() != null ? s.getRole().getName() : "";
                out.printf("%d,%s,%s,%s%n", s.getId(), escapeCsv(s.getName()), escapeCsv(s.getEmail()), escapeCsv(role));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write CSV report", e);
        }
    }

    private String escapeCsv(String v) {
        if (v == null) {
            return "";
        }
        return v.replace(",", " ");
    }
}
