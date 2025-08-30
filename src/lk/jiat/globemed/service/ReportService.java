package lk.jiat.globemed.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lk.jiat.globemed.dao.*;
import lk.jiat.globemed.model.*;

public class ReportService {

    private final PatientDao patientDao = new PatientDao();
    private final StaffDao staffDao = new StaffDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final BillingDao billingDao = new BillingDao();

    public String generatePatientReport() {
        StringBuilder report = new StringBuilder();

        try {
            List<Patient> patients = patientDao.findAll();

            report.append("=== GLOBEMED PATIENT REPORT ===\n");
            report.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

            report.append("📊 PATIENT SUMMARY:\n");
            report.append("Total Patients: ").append(patients.size()).append("\n\n");

            long maleCount = patients.stream().mapToLong(p -> "Male".equalsIgnoreCase(p.getGender()) ? 1 : 0).sum();
            long femaleCount = patients.stream().mapToLong(p -> "Female".equalsIgnoreCase(p.getGender()) ? 1 : 0).sum();

            report.append("👥 GENDER DISTRIBUTION:\n");
            report.append("Male: ").append(maleCount).append(" (").append(String.format("%.1f", (maleCount * 100.0 / patients.size()))).append("%)\n");
            report.append("Female: ").append(femaleCount).append(" (").append(String.format("%.1f", (femaleCount * 100.0 / patients.size()))).append("%)\n\n");

            report.append("📋 PATIENT DETAILS:\n");
            report.append(String.format("%-5s %-25s %-15s %-30s %-15s\n", "ID", "Name", "Gender", "Email/Contact", "DOB"));
            report.append("─".repeat(90)).append("\n");

            for (Patient patient : patients) {
                String name = patient.getFirstName() + " " + patient.getLastName();
                if (name.length() > 24) {
                    name = name.substring(0, 21) + "...";
                }

                String contact = patient.getContactNumber() != null ? patient.getContactNumber() : "N/A";
                if (contact.length() > 29) {
                    contact = contact.substring(0, 26) + "...";
                }

                String dob = patient.getDob() != null ? patient.getDob().toString() : "N/A";

                report.append(String.format("%-5s %-25s %-15s %-30s %-15s\n",
                        patient.getId(),
                        name,
                        patient.getGender() != null ? patient.getGender() : "N/A",
                        contact,
                        dob));
            }

            report.append("\n=== END OF PATIENT REPORT ===");

        } catch (Exception e) {
            report.append("Error generating patient report: ").append(e.getMessage());
        }

        return report.toString();
    }

    public String generateStaffReport() {
        StringBuilder report = new StringBuilder();

        try {
            List<Staff> staffList = staffDao.findAll();

            report.append("=== GLOBEMED STAFF REPORT ===\n");
            report.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

            report.append("👨‍⚕️ STAFF SUMMARY:\n");
            report.append("Total Staff: ").append(staffList.size()).append("\n\n");

            report.append("📊 ROLE DISTRIBUTION:\n");
            staffList.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            s -> s.getRole() != null ? s.getRole().getName() : "No Role",
                            java.util.stream.Collectors.counting()))
                    .forEach((role, count)
                            -> report.append(role).append(": ").append(count).append("\n"));

            report.append("\n📋 STAFF DETAILS:\n");
            report.append(String.format("%-5s %-30s %-35s %-15s\n", "ID", "Name", "Email", "Role"));
            report.append("─".repeat(85)).append("\n");

            for (Staff staff : staffList) {
                String name = staff.getName();
                if (name != null && name.length() > 29) {
                    name = name.substring(0, 26) + "...";
                }

                String email = staff.getEmail();
                if (email != null && email.length() > 34) {
                    email = email.substring(0, 31) + "...";
                }

                String role = staff.getRole() != null ? staff.getRole().getName() : "No Role";

                report.append(String.format("%-5s %-30s %-35s %-15s\n",
                        staff.getId(),
                        name != null ? name : "N/A",
                        email != null ? email : "N/A",
                        role));
            }

            report.append("\n=== END OF STAFF REPORT ===");

        } catch (Exception e) {
            report.append("Error generating staff report: ").append(e.getMessage());
        }

        return report.toString();
    }

    public String generateFinancialReport() {
        StringBuilder report = new StringBuilder();

        try {
            List<BillingRecord> billingRecords = billingDao.findAll();

            report.append("=== GLOBEMED FINANCIAL REPORT ===\n");
            report.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

            report.append("💰 FINANCIAL SUMMARY:\n");
            report.append("Total Billing Records: ").append(billingRecords.size()).append("\n");

            double totalRevenue = billingRecords.stream()
                    .filter(b -> "Paid".equalsIgnoreCase(b.getStatus()))
                    .mapToDouble(BillingRecord::getAmount)
                    .sum();

            double pendingAmount = billingRecords.stream()
                    .filter(b -> "Pending".equalsIgnoreCase(b.getStatus()))
                    .mapToDouble(BillingRecord::getAmount)
                    .sum();

            long paidCount = billingRecords.stream()
                    .mapToLong(b -> "Paid".equalsIgnoreCase(b.getStatus()) ? 1 : 0)
                    .sum();

            long pendingCount = billingRecords.stream()
                    .mapToLong(b -> "Pending".equalsIgnoreCase(b.getStatus()) ? 1 : 0)
                    .sum();

            report.append("Total Revenue (Paid): $").append(String.format("%.2f", totalRevenue)).append("\n");
            report.append("Pending Amount: $").append(String.format("%.2f", pendingAmount)).append("\n");
            report.append("Paid Bills: ").append(paidCount).append("\n");
            report.append("Pending Bills: ").append(pendingCount).append("\n\n");

            report.append("💳 PAYMENT METHOD BREAKDOWN:\n");
            billingRecords.stream()
                    .filter(b -> "Paid".equalsIgnoreCase(b.getStatus()))
                    .collect(java.util.stream.Collectors.groupingBy(
                            b -> b.getPaymentMethod() != null ? b.getPaymentMethod() : "Unknown",
                            java.util.stream.Collectors.summingDouble(BillingRecord::getAmount)))
                    .forEach((method, amount)
                            -> report.append(method).append(": $").append(String.format("%.2f", amount)).append("\n"));

            report.append("\n📋 RECENT TRANSACTIONS:\n");
            report.append(String.format("%-5s %-15s %-12s %-15s %-20s\n", "ID", "Amount", "Status", "Method", "Date"));
            report.append("─".repeat(67)).append("\n");

            billingRecords.stream()
                    .sorted((a, b) -> {
                        if (a.getCreatedAt() == null && b.getCreatedAt() == null) {
                            return 0;
                        }
                        if (a.getCreatedAt() == null) {
                            return 1;
                        }
                        if (b.getCreatedAt() == null) {
                            return -1;
                        }
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    })
                    .limit(10)
                    .forEach(billing -> {
                        String date = billing.getCreatedAt() != null
                                ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.sql.Timestamp.valueOf(billing.getCreatedAt())) : "N/A";

                        report.append(String.format("%-5s $%-14.2f %-12s %-15s %-20s\n",
                                billing.getId(),
                                billing.getAmount(),
                                billing.getStatus() != null ? billing.getStatus() : "N/A",
                                billing.getPaymentMethod() != null ? billing.getPaymentMethod() : "N/A",
                                date));
                    });

            report.append("\n=== END OF FINANCIAL REPORT ===");

        } catch (Exception e) {
            report.append("Error generating financial report: ").append(e.getMessage());
        }

        return report.toString();
    }

    public String generateAppointmentReport() {
        StringBuilder report = new StringBuilder();

        try {
            List<Appointment> appointments = appointmentDao.findAll();

            report.append("=== GLOBEMED APPOINTMENT REPORT ===\n");
            report.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

            report.append("📅 APPOINTMENT SUMMARY:\n");
            report.append("Total Appointments: ").append(appointments.size()).append("\n\n");

            report.append("📊 STATUS DISTRIBUTION:\n");
            appointments.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            a -> a.getStatus() != null ? a.getStatus() : "Unknown",
                            java.util.stream.Collectors.counting()))
                    .forEach((status, count)
                            -> report.append(status).append(": ").append(count).append("\n"));

            report.append("\n📋 APPOINTMENT DETAILS:\n");
            report.append(String.format("%-5s %-25s %-25s %-20s %-12s\n", "ID", "Patient", "Doctor", "Date/Time", "Status"));
            report.append("─".repeat(87)).append("\n");

            for (Appointment appointment : appointments) {
                String patientName = "N/A";
                if (appointment.getPatient() != null) {
                    patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
                    if (patientName.length() > 24) {
                        patientName = patientName.substring(0, 21) + "...";
                    }
                }

                String doctorName = "N/A";
                if (appointment.getDoctor() != null) {
                    doctorName = appointment.getDoctor().getName();
                    if (doctorName != null && doctorName.length() > 24) {
                        doctorName = doctorName.substring(0, 21) + "...";
                    }
                }

                String dateTime = "N/A";
                if (appointment.getAppointmentDateTime() != null) {
                    dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                            java.sql.Timestamp.valueOf(appointment.getAppointmentDateTime()));
                }

                report.append(String.format("%-5s %-25s %-25s %-20s %-12s\n",
                        appointment.getId(),
                        patientName,
                        doctorName,
                        dateTime,
                        appointment.getStatus() != null ? appointment.getStatus() : "N/A"));
            }

            report.append("\n=== END OF APPOINTMENT REPORT ===");

        } catch (Exception e) {
            report.append("Error generating appointment report: ").append(e.getMessage());
        }

        return report.toString();
    }
}
