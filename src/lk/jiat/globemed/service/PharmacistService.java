package lk.jiat.globemed.service;

import lk.jiat.globemed.dao.MedicationDao;
import lk.jiat.globemed.dao.PrescriptionDao;
import lk.jiat.globemed.model.Medication;
import lk.jiat.globemed.model.Prescription;
import lk.jiat.globemed.model.PrescriptionItem;
import lk.jiat.globemed.model.Staff;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for pharmacist-specific operations
 * 
 * @author upekhansaja
 */
public class PharmacistService {
    
    private final MedicationDao medicationDao = new MedicationDao();
    private final PrescriptionDao prescriptionDao = new PrescriptionDao();
    
    /**
     * Add a new medication to inventory
     */
    public Medication addMedication(Medication medication) {
        validateMedication(medication);
        return medicationDao.create(medication);
    }
    
    /**
     * Update existing medication
     */
    public Medication updateMedication(Medication medication) {
        validateMedication(medication);
        return medicationDao.update(medication);
    }
    
    /**
     * Dispense medication (reduce stock)
     */
    public void dispenseMedication(Long medicationId, int quantity, String notes) {
        Medication medication = medicationDao.findById(medicationId);
        if (medication == null) {
            throw new IllegalArgumentException("Medication not found");
        }
        
        if (medication.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + 
                medication.getStockQuantity() + ", Requested: " + quantity);
        }
        
        medication.dispense(quantity);
        medicationDao.update(medication);
        
        // Log the dispensation (could be expanded to create a dispensation record)
        System.out.println("✅ Dispensed " + quantity + " units of " + medication.getName() + 
                          (notes != null && !notes.isEmpty() ? " - Notes: " + notes : ""));
    }
    
    /**
     * Restock medication
     */
    public void restockMedication(Long medicationId, int quantity) {
        Medication medication = medicationDao.findById(medicationId);
        if (medication == null) {
            throw new IllegalArgumentException("Medication not found");
        }
        
        medication.restock(quantity);
        medicationDao.update(medication);
        
        System.out.println("✅ Restocked " + quantity + " units of " + medication.getName() + 
                          ". New stock: " + medication.getStockQuantity());
    }
    
    /**
     * Fill a prescription
     */
    public void fillPrescription(Long prescriptionId, Staff pharmacist) {
        Prescription prescription = prescriptionDao.findById(prescriptionId);
        if (prescription == null) {
            throw new IllegalArgumentException("Prescription not found");
        }
        
        if (!prescription.canBeFilled()) {
            throw new IllegalArgumentException("Prescription cannot be filled. Status: " + prescription.getStatus());
        }
        
        // Check if all medications are available in sufficient quantities
        for (PrescriptionItem item : prescription.getItems()) {
            if (!item.canBeFilled()) {
                throw new IllegalArgumentException("Insufficient stock for " + item.getMedication().getName() + 
                    ". Available: " + item.getMedication().getStockQuantity() + 
                    ", Required: " + item.getQuantity());
            }
        }
        
        // Dispense all medications
        for (PrescriptionItem item : prescription.getItems()) {
            item.getMedication().dispense(item.getQuantity());
            medicationDao.update(item.getMedication());
        }
        
        // Update prescription status
        prescription.markAsFilled(pharmacist);
        prescriptionDao.update(prescription);
        
        System.out.println("✅ Prescription #" + prescriptionId + " filled by " + pharmacist.getName());
    }
    
    /**
     * Get medications that are low in stock
     */
    public List<Medication> getLowStockMedications() {
        return medicationDao.findLowStock();
    }
    
    /**
     * Get expired medications
     */
    public List<Medication> getExpiredMedications() {
        return medicationDao.findExpired();
    }
    
    /**
     * Get medications expiring soon (within 3 months)
     */
    public List<Medication> getMedicationsExpiringSoon() {
        return medicationDao.findExpiringSoon();
    }
    
    /**
     * Get pending prescriptions
     */
    public List<Prescription> getPendingPrescriptions() {
        return prescriptionDao.findPending();
    }
    
    /**
     * Search medications by name
     */
    public List<Medication> searchMedicationsByName(String name) {
        return medicationDao.findByName(name);
    }
    
    /**
     * Get medications by category
     */
    public List<Medication> getMedicationsByCategory(String category) {
        return medicationDao.findByCategory(category);
    }
    
    /**
     * Get pharmacy statistics
     */
    public Map<String, Object> getPharmacyStatistics() {
        List<Medication> allMedications = medicationDao.findAll();
        List<Prescription> allPrescriptions = prescriptionDao.findAll();
        
        return Map.of(
            "totalMedications", allMedications.size(),
            "availableMedications", allMedications.stream().mapToLong(m -> 
                "Available".equals(m.getStatus()) ? 1 : 0).sum(),
            "lowStockMedications", getLowStockMedications().size(),
            "expiredMedications", getExpiredMedications().size(),
            "expiringSoonMedications", getMedicationsExpiringSoon().size(),
            "totalPrescriptions", allPrescriptions.size(),
            "pendingPrescriptions", prescriptionDao.countByStatus("Pending"),
            "filledPrescriptions", prescriptionDao.countByStatus("Filled"),
            "totalInventoryValue", allMedications.stream()
                .mapToDouble(m -> m.getStockQuantity() * m.getUnitPrice()).sum()
        );
    }
    
    /**
     * Get medication categories
     */
    public List<String> getMedicationCategories() {
        return medicationDao.findAllCategories();
    }
    
    /**
     * Get manufacturers
     */
    public List<String> getManufacturers() {
        return medicationDao.findAllManufacturers();
    }
    
    /**
     * Generate pharmacy inventory report
     */
    public String generateInventoryReport() {
        List<Medication> medications = medicationDao.findAll();
        Map<String, Object> stats = getPharmacyStatistics();
        
        StringBuilder report = new StringBuilder();
        report.append("=== PHARMACY INVENTORY REPORT ===\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        report.append("📊 INVENTORY SUMMARY:\n");
        report.append("Total Medications: ").append(stats.get("totalMedications")).append("\n");
        report.append("Available: ").append(stats.get("availableMedications")).append("\n");
        report.append("Low Stock: ").append(stats.get("lowStockMedications")).append("\n");
        report.append("Expired: ").append(stats.get("expiredMedications")).append("\n");
        report.append("Expiring Soon: ").append(stats.get("expiringSoonMedications")).append("\n");
        report.append("Total Inventory Value: $").append(String.format("%.2f", stats.get("totalInventoryValue"))).append("\n\n");
        
        // Category breakdown
        Map<String, Long> categoryCount = medications.stream()
            .collect(Collectors.groupingBy(Medication::getCategory, Collectors.counting()));
        
        report.append("📋 CATEGORY BREAKDOWN:\n");
        categoryCount.forEach((category, count) -> 
            report.append(category).append(": ").append(count).append(" medications\n"));
        
        report.append("\n⚠️ LOW STOCK ALERTS:\n");
        List<Medication> lowStock = getLowStockMedications();
        if (lowStock.isEmpty()) {
            report.append("No medications are currently low in stock.\n");
        } else {
            for (Medication med : lowStock) {
                report.append("- ").append(med.getName()).append(" (").append(med.getStockQuantity())
                      .append("/").append(med.getMinimumStock()).append(")\n");
            }
        }
        
        report.append("\n🚨 EXPIRY ALERTS:\n");
        List<Medication> expired = getExpiredMedications();
        List<Medication> expiringSoon = getMedicationsExpiringSoon();
        
        if (!expired.isEmpty()) {
            report.append("EXPIRED:\n");
            for (Medication med : expired) {
                report.append("- ").append(med.getName()).append(" (Expired: ").append(med.getExpiryDate()).append(")\n");
            }
        }
        
        if (!expiringSoon.isEmpty()) {
            report.append("EXPIRING SOON:\n");
            for (Medication med : expiringSoon) {
                report.append("- ").append(med.getName()).append(" (Expires: ").append(med.getExpiryDate()).append(")\n");
            }
        }
        
        if (expired.isEmpty() && expiringSoon.isEmpty()) {
            report.append("No expiry concerns at this time.\n");
        }
        
        report.append("\n=== END OF REPORT ===");
        return report.toString();
    }
    
    /**
     * Generate low stock report
     */
    public String generateLowStockReport() {
        List<Medication> lowStockMedications = getLowStockMedications();
        
        StringBuilder report = new StringBuilder();
        report.append("=== LOW STOCK REPORT ===\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        if (lowStockMedications.isEmpty()) {
            report.append("✅ No medications are currently low in stock.\n");
        } else {
            report.append("⚠️ MEDICATIONS REQUIRING RESTOCK (").append(lowStockMedications.size()).append(" items):\n\n");
            
            for (Medication med : lowStockMedications) {
                report.append("📦 ").append(med.getName()).append("\n");
                report.append("   Category: ").append(med.getCategory()).append("\n");
                report.append("   Manufacturer: ").append(med.getManufacturer()).append("\n");
                report.append("   Current Stock: ").append(med.getStockQuantity()).append(" units\n");
                report.append("   Minimum Stock: ").append(med.getMinimumStock()).append(" units\n");
                report.append("   Shortage: ").append(med.getMinimumStock() - med.getStockQuantity()).append(" units\n");
                report.append("   Unit Price: $").append(String.format("%.2f", med.getUnitPrice())).append("\n");
                report.append("   Status: ").append(med.getStatus()).append("\n");
                if (med.getExpiryDate() != null) {
                    report.append("   Expiry Date: ").append(med.getExpiryDate()).append("\n");
                }
                report.append("\n");
            }
            
            // Calculate total restock cost
            double totalRestockCost = lowStockMedications.stream()
                .mapToDouble(med -> (med.getMinimumStock() - med.getStockQuantity()) * med.getUnitPrice())
                .sum();
            
            report.append("💰 ESTIMATED RESTOCK COST: $").append(String.format("%.2f", totalRestockCost)).append("\n");
        }
        
        report.append("\n=== END OF REPORT ===");
        return report.toString();
    }
    
    /**
     * Generate expiry report
     */
    public String generateExpiryReport() {
        List<Medication> expiredMedications = getExpiredMedications();
        List<Medication> expiringSoonMedications = getMedicationsExpiringSoon();
        
        StringBuilder report = new StringBuilder();
        report.append("=== MEDICATION EXPIRY REPORT ===\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        // Expired medications
        if (!expiredMedications.isEmpty()) {
            report.append("🚨 EXPIRED MEDICATIONS (").append(expiredMedications.size()).append(" items):\n\n");
            
            for (Medication med : expiredMedications) {
                report.append("❌ ").append(med.getName()).append("\n");
                report.append("   Category: ").append(med.getCategory()).append("\n");
                report.append("   Manufacturer: ").append(med.getManufacturer()).append("\n");
                report.append("   Stock: ").append(med.getStockQuantity()).append(" units\n");
                report.append("   Expired Date: ").append(med.getExpiryDate()).append("\n");
                report.append("   Batch Number: ").append(med.getBatchNumber() != null ? med.getBatchNumber() : "N/A").append("\n");
                report.append("   Value Lost: $").append(String.format("%.2f", med.getStockQuantity() * med.getUnitPrice())).append("\n");
                report.append("\n");
            }
            
            double totalExpiredValue = expiredMedications.stream()
                .mapToDouble(med -> med.getStockQuantity() * med.getUnitPrice())
                .sum();
            
            report.append("💸 TOTAL EXPIRED VALUE: $").append(String.format("%.2f", totalExpiredValue)).append("\n\n");
        } else {
            report.append("✅ No expired medications found.\n\n");
        }
        
        // Expiring soon medications
        if (!expiringSoonMedications.isEmpty()) {
            report.append("⚠️ MEDICATIONS EXPIRING SOON (").append(expiringSoonMedications.size()).append(" items):\n\n");
            
            for (Medication med : expiringSoonMedications) {
                report.append("⏰ ").append(med.getName()).append("\n");
                report.append("   Category: ").append(med.getCategory()).append("\n");
                report.append("   Manufacturer: ").append(med.getManufacturer()).append("\n");
                report.append("   Stock: ").append(med.getStockQuantity()).append(" units\n");
                report.append("   Expiry Date: ").append(med.getExpiryDate()).append("\n");
                report.append("   Batch Number: ").append(med.getBatchNumber() != null ? med.getBatchNumber() : "N/A").append("\n");
                report.append("   Current Value: $").append(String.format("%.2f", med.getStockQuantity() * med.getUnitPrice())).append("\n");
                report.append("\n");
            }
            
            double totalExpiringSoonValue = expiringSoonMedications.stream()
                .mapToDouble(med -> med.getStockQuantity() * med.getUnitPrice())
                .sum();
            
            report.append("💰 TOTAL VALUE AT RISK: $").append(String.format("%.2f", totalExpiringSoonValue)).append("\n");
        } else {
            report.append("✅ No medications expiring soon.\n");
        }
        
        report.append("\n📋 RECOMMENDATIONS:\n");
        if (!expiredMedications.isEmpty()) {
            report.append("• Remove expired medications from inventory immediately\n");
            report.append("• Follow proper disposal procedures for expired medications\n");
        }
        if (!expiringSoonMedications.isEmpty()) {
            report.append("• Prioritize dispensing medications expiring soon\n");
            report.append("• Consider promotional pricing to move expiring stock\n");
            report.append("• Review ordering patterns to reduce future waste\n");
        }
        if (expiredMedications.isEmpty() && expiringSoonMedications.isEmpty()) {
            report.append("• Continue current inventory management practices\n");
            report.append("• Regular monitoring is maintaining good expiry control\n");
        }
        
        report.append("\n=== END OF REPORT ===");
        return report.toString();
    }
    
    private void validateMedication(Medication medication) {
        if (medication.getName() == null || medication.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medication name is required");
        }
        if (medication.getCategory() == null || medication.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (medication.getManufacturer() == null || medication.getManufacturer().trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer is required");
        }
        if (medication.getUnitPrice() == null || medication.getUnitPrice() < 0) {
            throw new IllegalArgumentException("Valid unit price is required");
        }
        if (medication.getStockQuantity() == null || medication.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Valid stock quantity is required");
        }
        if (medication.getMinimumStock() == null || medication.getMinimumStock() < 0) {
            throw new IllegalArgumentException("Valid minimum stock is required");
        }
        if (medication.getDosageForm() == null || medication.getDosageForm().trim().isEmpty()) {
            throw new IllegalArgumentException("Dosage form is required");
        }
    }
}