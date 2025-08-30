package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Staff doctor;

    @ManyToOne
    @JoinColumn(name = "pharmacist_id")
    private Staff pharmacist;

    @Column(nullable = false)
    private LocalDateTime prescribedDate;

    private LocalDateTime dispensedDate;

    @Column(length = 20)
    private String status; // Pending, Partially Filled, Filled, Cancelled

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PrescriptionItem> items;

    public Prescription() {
    }

    public Prescription(Patient patient, Staff doctor) {
        this.patient = patient;
        this.doctor = doctor;
        this.prescribedDate = LocalDateTime.now();
        this.status = "Pending";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Staff getDoctor() {
        return doctor;
    }

    public void setDoctor(Staff doctor) {
        this.doctor = doctor;
    }

    public Staff getPharmacist() {
        return pharmacist;
    }

    public void setPharmacist(Staff pharmacist) {
        this.pharmacist = pharmacist;
    }

    public LocalDateTime getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(LocalDateTime prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public LocalDateTime getDispensedDate() {
        return dispensedDate;
    }

    public void setDispensedDate(LocalDateTime dispensedDate) {
        this.dispensedDate = dispensedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PrescriptionItem> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItem> items) {
        this.items = items;
    }

    public double getTotalCost() {
        return items != null ? items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getMedication().getUnitPrice())
                .sum() : 0.0;
    }

    public boolean canBeFilled() {
        return "Pending".equals(status) || "Partially Filled".equals(status);
    }

    public void markAsFilled(Staff pharmacist) {
        this.pharmacist = pharmacist;
        this.dispensedDate = LocalDateTime.now();
        this.status = "Filled";
    }

    @Override
    public String toString() {
        return "Prescription #" + id + " - " + patient.getFullName() + " (" + status + ")";
    }
}
