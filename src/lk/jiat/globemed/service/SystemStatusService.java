package lk.jiat.globemed.service;

import lk.jiat.globemed.dao.*;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to provide system status and statistics
 * 
 * @author upekhansaja
 */
public class SystemStatusService {
    
    private final PatientDao patientDao = new PatientDao();
    private final StaffDao staffDao = new StaffDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final BillingDao billingDao = new BillingDao();
    
    /**
     * Get comprehensive system statistics
     * @return Map containing various system statistics
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Patient statistics
            stats.put("totalPatients", patientDao.findAll().size());
            
            // Staff statistics
            stats.put("totalStaff", staffDao.findAll().size());
            stats.put("totalDoctors", getStaffCountByRole("Doctor"));
            stats.put("totalNurses", getStaffCountByRole("Nurse"));
            stats.put("totalPharmacists", getStaffCountByRole("Pharmacist"));
            stats.put("totalAccountants", getStaffCountByRole("Accountant"));
            stats.put("totalAdmins", getStaffCountByRole("Admin"));
            
            // Appointment statistics
            stats.put("totalAppointments", appointmentDao.findAll().size());
            stats.put("scheduledAppointments", getAppointmentCountByStatus("Scheduled"));
            stats.put("completedAppointments", getAppointmentCountByStatus("Completed"));
            stats.put("cancelledAppointments", getAppointmentCountByStatus("Cancelled"));
            
            // Billing statistics
            stats.put("totalBillingRecords", billingDao.findAll().size());
            stats.put("paidBills", getBillingCountByStatus("Paid"));
            stats.put("pendingBills", getBillingCountByStatus("Pending"));
            stats.put("totalRevenue", getTotalRevenue());
            
            // System health
            stats.put("databaseConnected", isDatabaseConnected());
            stats.put("systemInitialized", isSystemInitialized());
            
        } catch (Exception e) {
            System.err.println("Error getting system statistics: " + e.getMessage());
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Get count of staff by role
     */
    private long getStaffCountByRole(String roleName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long) session.createQuery(
                "SELECT COUNT(s) FROM Staff s WHERE s.role.name = :roleName")
                .setParameter("roleName", roleName)
                .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get count of appointments by status
     */
    private long getAppointmentCountByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long) session.createQuery(
                "SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
                .setParameter("status", status)
                .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get count of billing records by status
     */
    private long getBillingCountByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long) session.createQuery(
                "SELECT COUNT(b) FROM BillingRecord b WHERE b.status = :status")
                .setParameter("status", status)
                .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get total revenue from paid bills
     */
    private double getTotalRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double revenue = (Double) session.createQuery(
                "SELECT SUM(b.amount) FROM BillingRecord b WHERE b.status = 'Paid'")
                .getSingleResult();
            return revenue != null ? revenue : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Check if database is connected
     */
    private boolean isDatabaseConnected() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if system is properly initialized with roles and permissions
     */
    private boolean isSystemInitialized() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long roleCount = (Long) session.createQuery("SELECT COUNT(r) FROM Role r").getSingleResult();
            Long permissionCount = (Long) session.createQuery("SELECT COUNT(p) FROM Permission p").getSingleResult();
            return roleCount > 0 && permissionCount > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get system health summary
     */
    public String getSystemHealthSummary() {
        Map<String, Object> stats = getSystemStatistics();
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== GlobeMed System Status ===\n");
        summary.append("Database Connected: ").append(stats.get("databaseConnected")).append("\n");
        summary.append("System Initialized: ").append(stats.get("systemInitialized")).append("\n");
        summary.append("Total Patients: ").append(stats.get("totalPatients")).append("\n");
        summary.append("Total Staff: ").append(stats.get("totalStaff")).append("\n");
        summary.append("Total Appointments: ").append(stats.get("totalAppointments")).append("\n");
        summary.append("Total Revenue: $").append(String.format("%.2f", stats.get("totalRevenue"))).append("\n");
        summary.append("===============================");
        
        return summary.toString();
    }
}