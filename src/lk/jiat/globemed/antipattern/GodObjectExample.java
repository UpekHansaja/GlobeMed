package lk.jiat.globemed.antipattern;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lk.jiat.globemed.model.*;

public class GodObjectExample {

    public Patient createPatient(String firstName, String lastName, String email) {

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

        savePatientToDatabase(patient);

        sendWelcomeEmail(patient);

        logPatientCreation(patient);

        return patient;
    }

    public void updatePatient(Patient patient) {

        validatePatient(patient);
        savePatientToDatabase(patient);
        sendUpdateNotification(patient);
        logPatientUpdate(patient);
    }

    public void deletePatient(Long patientId) {

        Patient patient = findPatientById(patientId);
        validatePatientDeletion(patient);
        removePatientFromDatabase(patientId);
        sendDeletionNotification(patient);
        logPatientDeletion(patient);
    }

    public Appointment createAppointment(Long patientId, Long doctorId, LocalDateTime dateTime) {

        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID required");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID required");
        }
        if (dateTime == null) {
            throw new IllegalArgumentException("Date time required");
        }
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule in the past");
        }

        Patient patient = findPatientById(patientId);
        Staff doctor = findStaffById(doctorId);

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found");
        }
        if (!isValidRole(doctor, "Doctor")) {
            throw new IllegalArgumentException("Staff member is not a doctor");
        }

        if (!isDoctorAvailable(doctor, dateTime)) {
            throw new IllegalArgumentException("Doctor not available");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(dateTime);
        appointment.setStatus("Scheduled");

        saveAppointmentToDatabase(appointment);

        sendAppointmentConfirmation(appointment);

        createBillingRecord(appointment);

        logAppointmentCreation(appointment);

        return appointment;
    }

    public Staff createStaff(String name, String email, String roleName) {

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
        staff.setPassword(generateDefaultPassword());

        saveStaffToDatabase(staff);

        sendWelcomeEmailToStaff(staff);

        logStaffCreation(staff);

        return staff;
    }

    public BillingRecord createBillingRecord(Appointment appointment) {

        double amount = calculateAppointmentCost(appointment);

        BillingRecord billing = new BillingRecord();
        billing.setPatient(appointment.getPatient());
        billing.setAmount(amount);
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());

        saveBillingToDatabase(billing);

        sendBillingNotification(billing);

        logBillingCreation(billing);

        return billing;
    }

    public String generatePatientReport(Long patientId) {

        Patient patient = findPatientById(patientId);
        List<Appointment> appointments = findAppointmentsByPatientId(patientId);
        List<BillingRecord> billings = findBillingsByPatientId(patientId);

        StringBuilder report = new StringBuilder();
        report.append("Patient Report\n");
        report.append("Name: ").append(patient.getFullName()).append("\n");
        report.append("Appointments: ").append(appointments.size()).append("\n");

        double totalBilled = 0;
        for (BillingRecord billing : billings) {
            totalBilled += billing.getAmount();
        }
        report.append("Total Billed: $").append(totalBilled).append("\n");

        saveReportToFile(report.toString(), "patient_" + patientId + "_report.txt");

        logReportGeneration("Patient Report", patientId);

        return report.toString();
    }

    private void savePatientToDatabase(Patient patient) {

        System.out.println("Saving patient to database: " + patient.getFullName());
    }

    private Patient findPatientById(Long id) {

        System.out.println("Finding patient by ID: " + id);
        return new Patient(); // Dummy return
    }

    private void removePatientFromDatabase(Long id) {

        System.out.println("Removing patient from database: " + id);
    }

    private void saveAppointmentToDatabase(Appointment appointment) {

        System.out.println("Saving appointment to database");
    }

    private Staff findStaffById(Long id) {

        System.out.println("Finding staff by ID: " + id);
        return new Staff();
    }

    private void saveStaffToDatabase(Staff staff) {

        System.out.println("Saving staff to database: " + staff.getName());
    }

    private void saveBillingToDatabase(BillingRecord billing) {

        System.out.println("Saving billing to database");
    }

    private Role findRoleByName(String name) {

        System.out.println("Finding role by name: " + name);
        return new Role();
    }

    private List<Appointment> findAppointmentsByPatientId(Long patientId) {

        System.out.println("Finding appointments for patient: " + patientId);
        return new ArrayList<>();
    }

    private List<BillingRecord> findBillingsByPatientId(Long patientId) {

        System.out.println("Finding billings for patient: " + patientId);
        return new ArrayList<>();
    }

    private void sendWelcomeEmail(Patient patient) {

        System.out.println("Sending welcome email to: " + patient.getFirstName());
    }

    private void sendUpdateNotification(Patient patient) {

        System.out.println("Sending update notification to: " + patient.getFirstName());
    }

    private void sendDeletionNotification(Patient patient) {

        System.out.println("Sending deletion notification for: " + patient.getFirstName());
    }

    private void sendAppointmentConfirmation(Appointment appointment) {

        System.out.println("Sending appointment confirmation");
    }

    private void sendWelcomeEmailToStaff(Staff staff) {

        System.out.println("Sending welcome email to staff: " + staff.getName());
    }

    private void sendBillingNotification(BillingRecord billing) {

        System.out.println("Sending billing notification");
    }

    private void validatePatient(Patient patient) {

        System.out.println("Validating patient: " + patient.getFullName());
    }

    private void validatePatientDeletion(Patient patient) {

        System.out.println("Validating patient deletion: " + patient.getFullName());
    }

    private boolean isValidEmail(String email) {

        return email != null && email.contains("@");
    }

    private boolean isEmailAlreadyUsed(String email) {

        System.out.println("Checking if email is already used: " + email);
        return false;
    }

    private boolean isValidRole(Staff staff, String expectedRole) {

        return staff.getRole() != null && expectedRole.equals(staff.getRole().getName());
    }

    private boolean isDoctorAvailable(Staff doctor, LocalDateTime dateTime) {

        System.out.println("Checking doctor availability");
        return true;
    }

    private double calculateAppointmentCost(Appointment appointment) {

        System.out.println("Calculating appointment cost");
        return 100.0;
    }

    private String generateDefaultPassword() {

        return "temp123";
    }

    private void logPatientCreation(Patient patient) {

        System.out.println("Logging patient creation: " + patient.getFullName());
    }

    private void logPatientUpdate(Patient patient) {

        System.out.println("Logging patient update: " + patient.getFullName());
    }

    private void logPatientDeletion(Patient patient) {

        System.out.println("Logging patient deletion: " + patient.getFullName());
    }

    private void logAppointmentCreation(Appointment appointment) {

        System.out.println("Logging appointment creation");
    }

    private void logStaffCreation(Staff staff) {

        System.out.println("Logging staff creation: " + staff.getName());
    }

    private void logBillingCreation(BillingRecord billing) {

        System.out.println("Logging billing creation");
    }

    private void logReportGeneration(String reportType, Long entityId) {

        System.out.println("Logging report generation: " + reportType + " for " + entityId);
    }

    private void saveReportToFile(String content, String filename) {

        System.out.println("Saving report to file: " + filename);
    }

}
