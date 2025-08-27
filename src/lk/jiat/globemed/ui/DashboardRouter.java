package lk.jiat.globemed.ui;

import javax.swing.SwingUtilities;
import lk.jiat.globemed.model.Staff;

public class DashboardRouter {

    /**
     * Open the correct dashboard frame according to staff role name. Role names
     * expected: "Admin", "Doctor", "Nurse", "Pharmacist", "Accountant"
     */
    public static void openDashboardForStaff(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException("staff is null");
        }

        System.out.println("Staff Name: " + staff.getName());

        String roleName = "";
        if (staff.getRole() != null && staff.getRole().getName() != null) {
            roleName = staff.getRole().getName().trim();
        }

        final String rn = roleName;
        System.out.println("Current user's Role Name is: " + rn);
        SwingUtilities.invokeLater(() -> {
            switch (rn) {
                case "Admin":
                    new AdminDashboardForm(staff).setVisible(true);
                    break;
                case "Doctor":
                    new DoctorDashboardForm(staff).setVisible(true);
                    break;
                case "Nurse":
                    new NurseDashboardForm(staff).setVisible(true);
                    break;
                case "Pharmacist":
                    new PharmacistDashboardForm(staff).setVisible(true);
                    break;
                case "Accountant":
                    new AccountantDashboardForm(staff).setVisible(true);
                    break;
                default:
                    // Fallback to generic dashboard
                    new DashboardForm(staff).setVisible(true);
                    break;
            }
        });
    }
}
