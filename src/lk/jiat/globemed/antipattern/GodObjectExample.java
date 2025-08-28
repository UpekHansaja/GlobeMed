package lk.jiat.globemed.antipattern;

import lk.jiat.globemed.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * ANTI-PATTERN EXAMPLE: God Object
 * 
 * This class demonstrates the God Object anti-pattern where a single class
 * tries to do everything. This is BAD DESIGN and should be avoided.
 * 
 * Problems with this approach:
 * 1. Violates Single Responsibility Principle
 * 2. Hard to maintain and test
 * 3. High coupling, low cohesion
 * 4. Difficult to extend or modify
 * 5. Code duplication
 * 6. Hard to understand
 * 
 * REFACTORING SOLUTION: Break this into separate service classes,
 * each with a single responsibility (PatientService, AppointmentService, etc.)
 */
public class GodObjectExample {
    
    // This class tries to handle EVERYTHING - BAD!
    
    // Patient management
    public Patient createPatient(String firstName, String lastName, String email) {
        // Validation logic mixed with business logic - BAD!
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name required");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name required");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email required");
        }
        
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        // More patient setup logic...
        
        // Database logic mixed in - BAD!
        savePatientToDatabase(patient);
        
        // Notification logic mixed in - BAD!
        sendWelcomeEmail(patient);
        
        // Audit logic mixed in - BAD!
        logPatientCreation(patient);
        
        return patient;
    }
    
    public void updatePatient(Patient patient) {
        // More mixed responsibilities...
        validatePatient(patient);
        savePatientToDatabase(patient);
        sendUpdateNotification(patient);
        logPatientUpdate(patient);
    }
    
    public void deletePatient(Long patientId) {
        // Even more mixed responsibilities...
        Patient patient = findPatientById(patientId);
        validatePatientDeletion(patient);
        removePatientFromDatabase(patientId);
        sendDeletionNotification(patient);
        logPatientDeletion(patient);
    }
    
    // Appointment management
    public Appointment createAppointment(Long patientId, Long doctorId, LocalDateTime dateTime) {
        // Validation mixed with business logic - BAD!
        if (patientId == null) throw new IllegalArgumentException("Patient ID required");
        if (doctorId == null) throw new IllegalArgumentException("Doctor ID required");
        if (dateTime == null) throw new IllegalArgumentException("Date time required");
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule in the past");
        }
        
        // Database queries mixed in - BAD!
        Patient patient = findPatientById(patientId);
        Staff doctor = findStaffById(doctorId);
        
        // Business logic mixed with validation - BAD!
        if (patient == null) throw new IllegalArgumentException("Patient not found");
        if (doctor == null) throw new IllegalArgumentException("Doctor not found");
        if (!isValidRole(doctor, "Doctor")) {
            throw new IllegalArgumentException("Staff member is not a doctor");
        }
        
        // Scheduling logic mixed in - BAD!
        if (!isDoctorAvailable(doctor, dateTime)) {
            throw new IllegalArgumentException("Doctor not available");
        }
        
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(dateTime);
        appointment.setStatus("Scheduled");
        
        // Database logic mixed in - BAD!
        saveAppointmentToDatabase(appointment);
        
        // Notification logic mixed in - BAD!
        sendAppointmentConfirmation(appointment);
        
        // Billing logic mixed in - BAD!
        createBillingRecord(appointment);
        
        // Audit logic mixed in - BAD!
        logAppointmentCreation(appointment);
        
        return appointment;
    }
    
    // Staff management
    public Staff createStaff(String name, String email, String roleName) {
        // More validation mixed with business logic - BAD!
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name required");
        }
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Valid email required");
        }
        if (isEmailAlreadyUsed(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        Role role = findRoleByName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Invalid role");
        }
        
        Staff staff = new Staff();
        staff.setName(name);
        staff.setEmail(email);
        staff.setRole(role);
        staff.setPassword(generateDefaultPassword()); // Security logic mixed in - BAD!
        
        // Database logic mixed in - BAD!
        saveStaffToDatabase(staff);
        
        // Notification logic mixed in - BAD!
        sendWelcomeEmailToStaff(staff);
        
        // Audit logic mixed in - BAD!
        logStaffCreation(staff);
        
        return staff;
    }
    
    // Billing management
    public BillingRecord createBillingRecord(Appointment appointment) {
        // Business logic mixed with calculation logic - BAD!
        double amount = calculateAppointmentCost(appointment);
        
        BillingRecord billing = new BillingRecord();
        billing.setPatient(appointment.getPatient());
        billing.setAmount(amount);
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());
        
        // Database logic mixed in - BAD!
        saveBillingToDatabase(billing);
        
        // Notification logic mixed in - BAD!
        sendBillingNotification(billing);
        
        // Audit logic mixed in - BAD!
        logBillingCreation(billing);
        
        return billing;
    }
    
    // Report generation
    public String generatePatientReport(Long patientId) {
        // Report logic mixed with data access - BAD!
        Patient patient = findPatientById(patientId);
        List<Appointment> appointments = findAppointmentsByPatientId(patientId);
        List<BillingRecord> billings = findBillingsByPatientId(patientId);
        
        // String building mixed with business logic - BAD!
        StringBuilder report = new StringBuilder();
        report.append("Patient Report\n");
        report.append("Name: ").append(patient.getFullName()).append("\n");
        report.append("Appointments: ").append(appointments.size()).append("\n");
        
        double totalBilled = 0;
        for (BillingRecord billing : billings) {
            totalBilled += billing.getAmount();
        }
        report.append("Total Billed: $").append(totalBilled).append("\n");
        
        // File I/O mixed in - BAD!
        saveReportToFile(report.toString(), "patient_" + patientId + "_report.txt");
        
        // Audit logic mixed in - BAD!
        logReportGeneration("Patient Report", patientId);
        
        return report.toString();
    }
    
    // Database operations (should be in separate DAO classes)
    private void savePatientToDatabase(Patient patient) {
        // Database logic - should be in PatientDao
        System.out.println("Saving patient to database: " + patient.getFullName());
    }
    
    private Patient findPatientById(Long id) {
        // Database logic - should be in PatientDao
        System.out.println("Finding patient by ID: " + id);
        return new Patient(); // Dummy return
    }
    
    private void removePatientFromDatabase(Long id) {
        // Database logic - should be in PatientDao
        System.out.println("Removing patient from database: " + id);
    }
    
    private void saveAppointmentToDatabase(Appointment appointment) {
        // Database logic - should be in AppointmentDao
        System.out.println("Saving appointment to database");
    }
    
    private Staff findStaffById(Long id) {
        // Database logic - should be in StaffDao
        System.out.println("Finding staff by ID: " + id);
        return new Staff(); // Dummy return
    }
    
    private void saveStaffToDatabase(Staff staff) {
        // Database logic - should be in StaffDao
        System.out.println("Saving staff to database: " + staff.getName());
    }
    
    private void saveBillingToDatabase(BillingRecord billing) {
        // Database logic - should be in BillingDao
        System.out.println("Saving billing to database");
    }
    
    private Role findRoleByName(String name) {
        // Database logic - should be in RoleDao
        System.out.println("Finding role by name: " + name);
        return new Role(); // Dummy return
    }
    
    private List<Appointment> findAppointmentsByPatientId(Long patientId) {
        // Database logic - should be in AppointmentDao
        System.out.println("Finding appointments for patient: " + patientId);
        return new ArrayList<>(); // Dummy return
    }
    
    private List<BillingRecord> findBillingsByPatientId(Long patientId) {
        // Database logic - should be in BillingDao
        System.out.println("Finding billings for patient: " + patientId);
        return new ArrayList<>(); // Dummy return
    }
    
    // Notification operations (should be in separate NotificationService)
    private void sendWelcomeEmail(Patient patient) {
        // Notification logic - should be in NotificationService
        System.out.println("Sending welcome email to: " + patient.getFirstName());
    }
    
    private void sendUpdateNotification(Patient patient) {
        // Notification logic - should be in NotificationService
        System.out.println("Sending update notification to: " + patient.getFirstName());
    }
    
    private void sendDeletionNotification(Patient patient) {
        // Notification logic - should be in NotificationService
        System.out.println("Sending deletion notification for: " + patient.getFirstName());
    }
    
    private void sendAppointmentConfirmation(Appointment appointment) {
        // Notification logic - should be in NotificationService
        System.out.println("Sending appointment confirmation");
    }
    
    private void sendWelcomeEmailToStaff(Staff staff) {
        // Notification logic - should be in NotificationService
        System.out.println("Sending welcome email to staff: " + staff.getName());
    }
    
    private void sendBillingNotification(BillingRecord billing) {
        // Notification logic - should be in NotificationService
        System.out.println("Sending billing notification");
    }
    
    // Validation operations (should be in separate ValidationService)
    private void validatePatient(Patient patient) {
        // Validation logic - should be in ValidationService
        System.out.println("Validating patient: " + patient.getFullName());
    }
    
    private void validatePatientDeletion(Patient patient) {
        // Validation logic - should be in ValidationService
        System.out.println("Validating patient deletion: " + patient.getFullName());
    }
    
    private boolean isValidEmail(String email) {
        // Validation logic - should be in ValidationService
        return email != null && email.contains("@");
    }
    
    private boolean isEmailAlreadyUsed(String email) {
        // Validation logic - should be in ValidationService
        System.out.println("Checking if email is already used: " + email);
        return false; // Dummy return
    }
    
    private boolean isValidRole(Staff staff, String expectedRole) {
        // Validation logic - should be in ValidationService
        return staff.getRole() != null && expectedRole.equals(staff.getRole().getName());
    }
    
    // Business logic operations (should be in separate business services)
    private boolean isDoctorAvailable(Staff doctor, LocalDateTime dateTime) {
        // Business logic - should be in SchedulingService
        System.out.println("Checking doctor availability");
        return true; // Dummy return
    }
    
    private double calculateAppointmentCost(Appointment appointment) {
        // Business logic - should be in BillingService
        System.out.println("Calculating appointment cost");
        return 100.0; // Dummy return
    }
    
    private String generateDefaultPassword() {
        // Security logic - should be in SecurityService
        return "temp123"; // Dummy return - NEVER do this in real code!
    }
    
    // Audit operations (should be in separate AuditService)
    private void logPatientCreation(Patient patient) {
        // Audit logic - should be in AuditService
        System.out.println("Logging patient creation: " + patient.getFullName());
    }
    
    private void logPatientUpdate(Patient patient) {
        // Audit logic - should be in AuditService
        System.out.println("Logging patient update: " + patient.getFullName());
    }
    
    private void logPatientDeletion(Patient patient) {
        // Audit logic - should be in AuditService
        System.out.println("Logging patient deletion: " + patient.getFullName());
    }
    
    private void logAppointmentCreation(Appointment appointment) {
        // Audit logic - should be in AuditService
        System.out.println("Logging appointment creation");
    }
    
    private void logStaffCreation(Staff staff) {
        // Audit logic - should be in AuditService
        System.out.println("Logging staff creation: " + staff.getName());
    }
    
    private void logBillingCreation(BillingRecord billing) {
        // Audit logic - should be in AuditService
        System.out.println("Logging billing creation");
    }
    
    private void logReportGeneration(String reportType, Long entityId) {
        // Audit logic - should be in AuditService
        System.out.println("Logging report generation: " + reportType + " for " + entityId);
    }
    
    // File operations (should be in separate FileService)
    private void saveReportToFile(String content, String filename) {
        // File I/O logic - should be in FileService
        System.out.println("Saving report to file: " + filename);
    }
    
    /*
     * REFACTORING SOLUTION:
     * 
     * Instead of this God Object, create separate service classes:
     * 
     * 1. PatientService - handles patient-related operations
     * 2. AppointmentService - handles appointment-related operations  
     * 3. StaffService - handles staff-related operations
     * 4. BillingService - handles billing-related operations
     * 5. NotificationService - handles all notifications
     * 6. ValidationService - handles all validations
     * 7. AuditService - handles all audit logging
     * 8. ReportService - handles report generation
     * 9. FileService - handles file operations
     * 10. SecurityService - handles security operations
     * 
     * Each service should have a single responsibility and be loosely coupled.
     * Use dependency injection to wire services together.
     * Use design patterns like Strategy, Observer, and Command to manage complexity.
     */
}