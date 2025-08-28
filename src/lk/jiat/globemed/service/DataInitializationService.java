package lk.jiat.globemed.service;

import lk.jiat.globemed.dao.*;
import lk.jiat.globemed.model.*;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Service to initialize the system with default data including roles, permissions,
 * default users, and sample data for demonstration purposes.
 * 
 * @author upekhansaja
 */
public class DataInitializationService {
    
    private final RoleDao roleDao = new RoleDao();
    private final PermissionDao permissionDao = new PermissionDao();
    private final StaffDao staffDao = new StaffDao();
    private final PatientDao patientDao = new PatientDao();
    
    /**
     * Initialize the system with default data if not already present
     */
    public void initializeSystemData() {
        try {
            System.out.println("🔄 Initializing system data...");
            
            initializePermissions();
            initializeRoles();
            initializeDefaultUsers();
            initializeSamplePatients();
            initializeHospitalStructure();
            
            System.out.println("✅ System data initialization completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing system data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize system permissions
     */
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
    
    /**
     * Initialize system roles with appropriate permissions
     */
    private void initializeRoles() {
        // Admin Role
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
        
        // Doctor Role
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
        
        // Nurse Role
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
        
        // Pharmacist Role
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
        
        // Accountant Role
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
    
    /**
     * Initialize default system users
     */
    private void initializeDefaultUsers() {
        // Admin User
        if (staffDao.findByEmail("admin@globemed.lk") == null) {
            Staff admin = new Staff();
            admin.setName("System Administrator");
            admin.setEmail("admin@globemed.lk");
            admin.setPassword("admin123"); // In production, this should be hashed
            admin.setRole(roleDao.findByName("Admin"));
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(admin);
                tx.commit();
                System.out.println("✅ Created default admin user: admin@globemed.lk / admin123");
            }
        }
        
        // Sample Doctor
        if (staffDao.findByEmail("doctor@globemed.lk") == null) {
            Staff doctor = new Staff();
            doctor.setName("Dr. Sarah Johnson");
            doctor.setEmail("doctor@globemed.lk");
            doctor.setPassword("doctor123");
            doctor.setRole(roleDao.findByName("Doctor"));
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(doctor);
                tx.commit();
                System.out.println("✅ Created sample doctor: doctor@globemed.lk / doctor123");
            }
        }
        
        // Sample Nurse
        if (staffDao.findByEmail("nurse@globemed.lk") == null) {
            Staff nurse = new Staff();
            nurse.setName("Nurse Mary Wilson");
            nurse.setEmail("nurse@globemed.lk");
            nurse.setPassword("nurse123");
            nurse.setRole(roleDao.findByName("Nurse"));
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(nurse);
                tx.commit();
                System.out.println("✅ Created sample nurse: nurse@globemed.lk / nurse123");
            }
        }
        
        // Sample Pharmacist
        if (staffDao.findByEmail("pharmacist@globemed.lk") == null) {
            Staff pharmacist = new Staff();
            pharmacist.setName("Pharmacist John Davis");
            pharmacist.setEmail("pharmacist@globemed.lk");
            pharmacist.setPassword("pharmacist123");
            pharmacist.setRole(roleDao.findByName("Pharmacist"));
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(pharmacist);
                tx.commit();
                System.out.println("✅ Created sample pharmacist: pharmacist@globemed.lk / pharmacist123");
            }
        }
        
        // Sample Accountant
        if (staffDao.findByEmail("accountant@globemed.lk") == null) {
            Staff accountant = new Staff();
            accountant.setName("Accountant Lisa Brown");
            accountant.setEmail("accountant@globemed.lk");
            accountant.setPassword("accountant123");
            accountant.setRole(roleDao.findByName("Accountant"));
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(accountant);
                tx.commit();
                System.out.println("✅ Created sample accountant: accountant@globemed.lk / accountant123");
            }
        }
    }
    
    /**
     * Initialize sample patients for demonstration
     */
    private void initializeSamplePatients() {
        if (patientDao.findAll().isEmpty()) {
            // Sample Patient 1
            Patient patient1 = new Patient();
            patient1.setFirstName("John");
            patient1.setLastName("Doe");
            patient1.setDob(LocalDate.of(1985, 5, 15));
            patient1.setGender("Male");
            patient1.setContactNumber("555-0101");
            patient1.setAddress("123 Main Street, Colombo 01");
            
            // Sample Patient 2
            Patient patient2 = new Patient();
            patient2.setFirstName("Jane");
            patient2.setLastName("Smith");
            patient2.setDob(LocalDate.of(1990, 8, 22));
            patient2.setGender("Female");
            patient2.setContactNumber("555-0102");
            patient2.setAddress("456 Oak Avenue, Colombo 02");
            
            // Sample Patient 3
            Patient patient3 = new Patient();
            patient3.setFirstName("Robert");
            patient3.setLastName("Johnson");
            patient3.setDob(LocalDate.of(1978, 12, 10));
            patient3.setGender("Male");
            patient3.setContactNumber("555-0103");
            patient3.setAddress("789 Pine Road, Colombo 03");
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(patient1);
                session.persist(patient2);
                session.persist(patient3);
                tx.commit();
                System.out.println("✅ Created sample patients");
            }
        }
    }
    
    /**
     * Initialize hospital structure using Composite pattern
     */
    private void initializeHospitalStructure() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check if hospital structure already exists
            Long hospitalCount = (Long) session.createQuery("SELECT COUNT(h) FROM Hospital h").getSingleResult();
            
            if (hospitalCount == 0) {
                Transaction tx = session.beginTransaction();
                
                // Create main hospital
                Hospital mainHospital = new Hospital("GlobeMed Central Hospital", 
                    "Main healthcare facility", "GMC001", "123 Medical Drive, Colombo 01");
                
                // Create departments
                Department cardiology = new Department("Cardiology", 
                    "Heart and cardiovascular care", "CARD", 150000.0);
                Department neurology = new Department("Neurology", 
                    "Brain and nervous system care", "NEUR", 120000.0);
                Department pediatrics = new Department("Pediatrics", 
                    "Children's healthcare", "PEDI", 100000.0);
                Department emergency = new Department("Emergency", 
                    "Emergency and trauma care", "EMER", 200000.0);
                
                // Add departments to hospital
                mainHospital.add(cardiology);
                mainHospital.add(neurology);
                mainHospital.add(pediatrics);
                mainHospital.add(emergency);
                
                session.persist(mainHospital);
                tx.commit();
                
                System.out.println("✅ Created hospital structure with departments");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating hospital structure: " + e.getMessage());
        }
    }
}