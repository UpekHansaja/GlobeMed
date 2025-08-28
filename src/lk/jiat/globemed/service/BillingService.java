package lk.jiat.globemed.service;

import lk.jiat.globemed.dao.BillingDao;
import lk.jiat.globemed.model.BillingRecord;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Prescription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for billing operations
 * 
 * @author upekhansaja
 */
public class BillingService {
    
    private final BillingDao billingDao = new BillingDao();
    
    /**
     * Create billing record for appointment
     */
    public BillingRecord createAppointmentBilling(Appointment appointment, double consultationFee) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(appointment.getPatient());
        billing.setAmount(consultationFee);
        billing.setPaymentMethod("Cash"); // Default
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());
        
        return billingDao.create(billing);
    }
    
    /**
     * Create billing record for prescription
     */
    public BillingRecord createPrescriptionBilling(Prescription prescription) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(prescription.getPatient());
        billing.setAmount(prescription.getTotalCost());
        billing.setPaymentMethod("Cash"); // Default
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());
        
        return billingDao.create(billing);
    }
    
    /**
     * Create custom billing record
     */
    public BillingRecord createCustomBilling(Patient patient, double amount, String description) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(patient);
        billing.setAmount(amount);
        billing.setPaymentMethod("Cash"); // Default
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());
        
        return billingDao.create(billing);
    }
    
    /**
     * Update billing record
     */
    public BillingRecord updateBilling(BillingRecord billing) {
        return billingDao.update(billing);
    }
    
    /**
     * Get all billing records
     */
    public List<BillingRecord> getAllBillingRecords() {
        return billingDao.findAll();
    }
    
    /**
     * Get billing record by ID
     */
    public BillingRecord getBillingById(Long id) {
        return billingDao.findById(id);
    }
    
    /**
     * Get billing records by patient
     */
    public List<BillingRecord> getPatientBillingRecords(Long patientId) {
        return billingDao.findByPatientId(patientId);
    }
    
    /**
     * Get billing records by status
     */
    public List<BillingRecord> getBillingRecordsByStatus(String status) {
        return billingDao.findByStatus(status);
    }
}
