package lk.jiat.globemed.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.jiat.globemed.dao.AppointmentDao;
import lk.jiat.globemed.dao.BillingDao;
import lk.jiat.globemed.dao.PrescriptionDao;
import lk.jiat.globemed.model.BillingRecord;
import lk.jiat.globemed.model.Patient;

public class AccountantService {

    private final BillingDao billingDao = new BillingDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final PrescriptionDao prescriptionDao = new PrescriptionDao();

    public BillingRecord createBillingRecord(Patient patient, double amount, String paymentMethod, String description) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(patient);
        billing.setAmount(amount);
        billing.setPaymentMethod(paymentMethod);
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());

        return billingDao.create(billing);
    }

    public void processPayment(Long billingId, String paymentMethod) {
        BillingRecord billing = billingDao.findById(billingId);
        if (billing == null) {
            throw new IllegalArgumentException("Billing record not found");
        }

        if (!"Pending".equals(billing.getStatus())) {
            throw new IllegalArgumentException("Payment already processed or rejected");
        }

        billing.setPaymentMethod(paymentMethod);
        billing.setStatus("Paid");
        billingDao.update(billing);

        System.out.println("Payment processed for billing #" + billingId + " - Amount: $"
                + String.format("%.2f", billing.getAmount()));
    }

    public void rejectPayment(Long billingId, String reason) {
        BillingRecord billing = billingDao.findById(billingId);
        if (billing == null) {
            throw new IllegalArgumentException("Billing record not found");
        }

        billing.setStatus("Rejected");
        billingDao.update(billing);

        System.out.println("Payment rejected for billing #" + billingId + " - Reason: " + reason);
    }

    public List<BillingRecord> getAllBillingRecords() {
        return billingDao.findAll();
    }

    public List<BillingRecord> getBillingRecordsByStatus(String status) {
        return billingDao.findByStatus(status);
    }

    public List<BillingRecord> getPendingPayments() {
        return getBillingRecordsByStatus("Pending");
    }

    public List<BillingRecord> getPaidRecords() {
        return getBillingRecordsByStatus("Paid");
    }

    public List<BillingRecord> getPatientBillingHistory(Long patientId) {
        return billingDao.findByPatientId(patientId);
    }

    public Map<String, Object> getFinancialStatistics() {
        List<BillingRecord> allRecords = billingDao.findAll();
        List<BillingRecord> paidRecords = getBillingRecordsByStatus("Paid");
        List<BillingRecord> pendingRecords = getBillingRecordsByStatus("Pending");
        List<BillingRecord> rejectedRecords = getBillingRecordsByStatus("Rejected");

        double totalRevenue = paidRecords.stream().mapToDouble(BillingRecord::getAmount).sum();
        double pendingAmount = pendingRecords.stream().mapToDouble(BillingRecord::getAmount).sum();
        double rejectedAmount = rejectedRecords.stream().mapToDouble(BillingRecord::getAmount).sum();

        return Map.of(
                "totalRecords", allRecords.size(),
                "paidRecords", paidRecords.size(),
                "pendingRecords", pendingRecords.size(),
                "rejectedRecords", rejectedRecords.size(),
                "totalRevenue", totalRevenue,
                "pendingAmount", pendingAmount,
                "rejectedAmount", rejectedAmount,
                "averageTransactionAmount", allRecords.isEmpty() ? 0.0
                : allRecords.stream().mapToDouble(BillingRecord::getAmount).average().orElse(0.0)
        );
    }

    public String generateFinancialReport() {
        Map<String, Object> stats = getFinancialStatistics();
        List<BillingRecord> allRecords = billingDao.findAll();

        StringBuilder report = new StringBuilder();
        report.append("=== FINANCIAL REPORT ===\n\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        report.append("📊 FINANCIAL SUMMARY:\n\n");

        report.append("Total Records: ").append(stats.get("totalRecords")).append("\n");
        report.append("Paid Records: ").append(stats.get("paidRecords")).append("\n");
        report.append("Pending Records: ").append(stats.get("pendingRecords")).append("\n");
        report.append("Rejected Records: ").append(stats.get("rejectedRecords")).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", stats.get("totalRevenue"))).append("\n");
        report.append("Pending Amount: $").append(String.format("%.2f", stats.get("pendingAmount"))).append("\n");
        report.append("Rejected Amount: $").append(String.format("%.2f", stats.get("rejectedAmount"))).append("\n");
        report.append("Average Transaction: $").append(String.format("%.2f", stats.get("averageTransactionAmount"))).append("\n\n");

        Map<String, Long> paymentMethodCount = allRecords.stream()
                .filter(r -> "Paid".equals(r.getStatus()))
                .collect(Collectors.groupingBy(BillingRecord::getPaymentMethod, Collectors.counting()));

        report.append("💳 PAYMENT METHOD BREAKDOWN:\n");
        paymentMethodCount.forEach((method, count)
                -> report.append(method).append(": ").append(count).append(" transactions\n"));

        report.append("\n📋 RECENT TRANSACTIONS (Last 10):\n");
        List<BillingRecord> recentRecords = allRecords.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());

        for (BillingRecord record : recentRecords) {
            report.append("- #").append(record.getId())
                    .append(" | ").append(record.getPatient().getFullName())
                    .append(" | $").append(String.format("%.2f", record.getAmount()))
                    .append(" | ").append(record.getStatus())
                    .append(" | ").append(record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .append("\n");
        }

        report.append("\n\n=== END OF REPORT ===");
        return report.toString();
    }

    public String generateRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<BillingRecord> records = billingDao.findAll().stream()
                .filter(r -> r.getCreatedAt().isAfter(startDate) && r.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("=== REVENUE REPORT ===\n");
        report.append("Period: ").append(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .append(" to ").append(endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        double totalRevenue = records.stream()
                .filter(r -> "Paid".equals(r.getStatus()))
                .mapToDouble(BillingRecord::getAmount)
                .sum();

        double pendingRevenue = records.stream()
                .filter(r -> "Pending".equals(r.getStatus()))
                .mapToDouble(BillingRecord::getAmount)
                .sum();

        report.append("💰 REVENUE SUMMARY:\n\n");

        report.append("Total Transactions: ").append(records.size()).append("\n");
        report.append("Confirmed Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("Pending Revenue: $").append(String.format("%.2f", pendingRevenue)).append("\n");
        report.append("Potential Total: $").append(String.format("%.2f", totalRevenue + pendingRevenue)).append("\n\n");

        Map<String, Double> dailyRevenue = records.stream()
                .filter(r -> "Paid".equals(r.getStatus()))
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Collectors.summingDouble(BillingRecord::getAmount)
                ));

        report.append("📅 DAILY BREAKDOWN:\n\n");
        dailyRevenue.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByKey())
                .forEach(entry -> report.append(entry.getKey()).append(": $")
                .append(String.format("%.2f", entry.getValue())).append("\n"));

        report.append("\n\n=== END OF REPORT ===");
        return report.toString();
    }

    public String generateOutstandingPaymentsReport() {
        List<BillingRecord> pendingRecords = getPendingPayments();

        StringBuilder report = new StringBuilder();
        report.append("=== OUTSTANDING PAYMENTS REPORT ===\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        if (pendingRecords.isEmpty()) {
            report.append("No outstanding payments found.\n");
        } else {
            double totalOutstanding = pendingRecords.stream().mapToDouble(BillingRecord::getAmount).sum();

            report.append("⚠️ OUTSTANDING PAYMENTS (").append(pendingRecords.size()).append(" records):\n");
            report.append("Total Outstanding Amount: $").append(String.format("%.2f", totalOutstanding)).append("\n\n");

            report.append("DETAILED LIST:\n");
            for (BillingRecord record : pendingRecords) {
                report.append("- #").append(record.getId())
                        .append(" | ").append(record.getPatient().getFullName())
                        .append(" | $").append(String.format("%.2f", record.getAmount()))
                        .append(" | Created: ").append(record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .append("\n");
            }

            report.append("\nRECOMMENDATIONS:\n");
            report.append("• Follow up with patients for pending payments\n");
            report.append("• Consider payment plans for large amounts\n");
            report.append("• Review payment processing procedures\n");
        }

        report.append("\n=== END OF REPORT ===");
        return report.toString();
    }
}
