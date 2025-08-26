package lk.jiat.globemed.app;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import javax.swing.*;
import lk.jiat.globemed.dao.StaffDao;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.ui.LoginForm;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
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

            StaffDao staffDao = new StaffDao();
            if (staffDao.findByEmail("admin@globemed.lk") == null) {
                Staff admin = new Staff();
                admin.setName("System Admin");
                admin.setEmail("admin@globemed.lk");
                admin.setPassword("admin123"); // ⚠️ plain for now
                // assign role later
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction tx = session.beginTransaction();
                    session.persist(admin);
                    tx.commit();
                }
                System.out.println("✅ Default admin user created: admin@globemed.lk / admin123");
            }

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
