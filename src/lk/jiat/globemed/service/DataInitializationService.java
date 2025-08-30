package lk.jiat.globemed.service;

import java.util.Arrays;
import java.util.HashSet;
import lk.jiat.globemed.dao.*;
import lk.jiat.globemed.model.*;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataInitializationService {

    private final RoleDao roleDao = new RoleDao();
    private final PermissionDao permissionDao = new PermissionDao();
    private final StaffDao staffDao = new StaffDao();
    private final PatientDao patientDao = new PatientDao();

    public void initializeSystemData() {
        try {
            System.out.println("🔄 Initializing system data...");

            initializePermissions();
            initializeRoles();
            initializeDefaultUsers();

            // initializeSamplePatients();
            // initializeHospitalStructure();
            System.out.println("✅ System data initialization completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error initializing system data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializePermissions() {
        String[] permissions = {
            "VIEW_PATIENTS", "MANAGE_PATIENTS", "DELETE_PATIENTS",
            "VIEW_APPOINTMENTS", "MANAGE_APPOINTMENTS", "DELETE_APPOINTMENTS",
            "VIEW_STAFF", "MANAGE_STAFF", "DELETE_STAFF",
            "VIEW_BILLING", "MANAGE_BILLING", "DELETE_BILLING",
            "VIEW_REPORTS", "GENERATE_REPORTS", "EXPORT_REPORTS",
            "SYSTEM_ADMIN", "AUDIT_LOGS", "MANAGE_ROLES"
        };

        for (String permName : permissions) {
            if (permissionDao.findByName(permName) == null) {
                Permission permission = new Permission();
                permission.setName(permName);

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction tx = session.beginTransaction();
                    session.persist(permission);
                    tx.commit();
                    System.out.println("✅ Created permission: " + permName);
                }
            }
        }
    }

    private void initializeRoles() {
        // Admin
        if (roleDao.findByName("Admin") == null) {
            Role adminRole = new Role();
            adminRole.setName("Admin");
            adminRole.setPermissions(new HashSet<>(permissionDao.findAll()));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(adminRole);
                tx.commit();
                System.out.println("✅ Created Admin role with all permissions");
            }
        }

        // Doctor
        if (roleDao.findByName("Doctor") == null) {
            Role doctorRole = new Role();
            doctorRole.setName("Doctor");
            doctorRole.setPermissions(new HashSet<>(Arrays.asList(
                    permissionDao.findByName("VIEW_PATIENTS"),
                    permissionDao.findByName("MANAGE_PATIENTS"),
                    permissionDao.findByName("VIEW_APPOINTMENTS"),
                    permissionDao.findByName("MANAGE_APPOINTMENTS"),
                    permissionDao.findByName("VIEW_BILLING"),
                    permissionDao.findByName("VIEW_REPORTS")
            )));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(doctorRole);
                tx.commit();
                System.out.println("✅ Created Doctor role");
            }
        }

        // Nurse
        if (roleDao.findByName("Nurse") == null) {
            Role nurseRole = new Role();
            nurseRole.setName("Nurse");
            nurseRole.setPermissions(new HashSet<>(Arrays.asList(
                    permissionDao.findByName("VIEW_PATIENTS"),
                    permissionDao.findByName("MANAGE_PATIENTS"),
                    permissionDao.findByName("VIEW_APPOINTMENTS"),
                    permissionDao.findByName("MANAGE_APPOINTMENTS")
            )));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(nurseRole);
                tx.commit();
                System.out.println("✅ Created Nurse role");
            }
        }

        // Pharmacist
        if (roleDao.findByName("Pharmacist") == null) {
            Role pharmacistRole = new Role();
            pharmacistRole.setName("Pharmacist");
            pharmacistRole.setPermissions(new HashSet<>(Arrays.asList(
                    permissionDao.findByName("VIEW_PATIENTS"),
                    permissionDao.findByName("VIEW_APPOINTMENTS"),
                    permissionDao.findByName("VIEW_BILLING")
            )));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(pharmacistRole);
                tx.commit();
                System.out.println("✅ Created Pharmacist role");
            }
        }

        // Accountant
        if (roleDao.findByName("Accountant") == null) {
            Role accountantRole = new Role();
            accountantRole.setName("Accountant");
            accountantRole.setPermissions(new HashSet<>(Arrays.asList(
                    permissionDao.findByName("VIEW_PATIENTS"),
                    permissionDao.findByName("VIEW_BILLING"),
                    permissionDao.findByName("MANAGE_BILLING"),
                    permissionDao.findByName("VIEW_REPORTS"),
                    permissionDao.findByName("GENERATE_REPORTS")
            )));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(accountantRole);
                tx.commit();
                System.out.println("✅ Created Accountant role");
            }
        }
    }

    private void initializeDefaultUsers() {
        // Admin
        if (staffDao.findByEmail("admin@globemed.lk") == null) {
            Staff admin = new Staff();
            admin.setName("System Administrator");
            admin.setEmail("admin@globemed.lk");
            admin.setPassword("admin123");
            admin.setRole(roleDao.findByName("Admin"));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(admin);
                tx.commit();
                System.out.println("✅ Created default admin user: admin@globemed.lk / admin123");
            }
        }

        System.out.println("ℹ️  Additional sample users can be created through the Admin Dashboard");
    }

    private void initializeSamplePatients() {
        System.out.println("ℹ️  Sample patients can be added through the Patient Management interface");
    }

    private void initializeHospitalStructure() {
        System.out.println("ℹ️  Hospital structure can be configured through the Admin Dashboard");
    }
}
