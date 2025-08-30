package lk.jiat.globemed.service;

import java.time.LocalDateTime;
import java.util.List;
import lk.jiat.globemed.dao.BillingDao;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.BillingRecord;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Prescription;

public class BillingService {

    private final BillingDao billingDao = new BillingDao();

    public BillingRecord createAppointmentBilling(Appointment appointment, double consultationFee) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(appointment.getPatient());
        billing.setAmount(consultationFee);
        billing.setPaymentMethod("Cash");
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());

        return billingDao.create(billing);
    }

    public BillingRecord createPrescriptionBilling(Prescription prescription) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(prescription.getPatient());
        billing.setAmount(prescription.getTotalCost());
        billing.setPaymentMethod("Cash"); // Default
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());

        return billingDao.create(billing);
    }

    public BillingRecord createCustomBilling(Patient patient, double amount, String description) {
        BillingRecord billing = new BillingRecord();
        billing.setPatient(patient);
        billing.setAmount(amount);
        billing.setPaymentMethod("Cash");
        billing.setStatus("Pending");
        billing.setCreatedAt(LocalDateTime.now());

        return billingDao.create(billing);
    }

    public BillingRecord updateBilling(BillingRecord billing) {
        return billingDao.update(billing);
    }

    public List<BillingRecord> getAllBillingRecords() {
        return billingDao.findAll();
    }

    public BillingRecord getBillingById(Long id) {
        return billingDao.findById(id);
    }

    public List<BillingRecord> getPatientBillingRecords(Long patientId) {
        return billingDao.findByPatientId(patientId);
    }

    public List<BillingRecord> getBillingRecordsByStatus(String status) {
        return billingDao.findByStatus(status);
    }
}
