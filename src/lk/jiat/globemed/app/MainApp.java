package lk.jiat.globemed.app;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import javax.swing.*;
import lk.jiat.globemed.ui.LoginForm;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * GlobeMed Healthcare Management System
 * 
 * A comprehensive healthcare management system implementing advanced design patterns
 * including Composite, Bridge, Builder, Chain of Responsibility, Flyweight, 
 * Interpreter, Mediator, and Prototype patterns.
 * 
 * Features:
 * - Patient Management
 * - Staff Management with Role-based Access Control
 * - Appointment Scheduling and Management
 * - Billing and Financial Management
 * - Audit Logging and Security
 * - Notification System (Email/SMS)
 * - Approval Workflows
 * - Reporting System
 * 
 * @author upekhansaja
 */
public class MainApp {

    public static void main(String[] args) {

        FlatGitHubDarkIJTheme.setup();

        try {
            HibernateUtil.getSessionFactory();
            testDatabaseConnection();
            System.out.println("✅ Database connection successful!");

            // Initialize system data (roles, permissions, default users, sample data)
            lk.jiat.globemed.service.DataInitializationService dataInit = 
                new lk.jiat.globemed.service.DataInitializationService();
            dataInit.initializeSystemData();
            
            // Show system status
            lk.jiat.globemed.service.SystemStatusService statusService = 
                new lk.jiat.globemed.service.SystemStatusService();
            System.out.println(statusService.getSystemHealthSummary());

        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();

            JOptionPane.showMessageDialog(null,
                    "Failed to connect to Database.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            // Launch the GlobeMed Healthcare Management System directly
            new LoginForm().setVisible(true);
        });
    }

    private static void testDatabaseConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            try {

                Long result = (Long) session.createNativeQuery("SELECT 1").getSingleResult();
                System.out.println("DB Test Query Result: " + result);

                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
