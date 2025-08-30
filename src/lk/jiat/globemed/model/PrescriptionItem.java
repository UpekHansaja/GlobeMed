package lk.jiat.globemed.model;

import jakarta.persistence.*;

@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 200)
    private String dosageInstructions; // 1 tablet per after a meal

    @Column(nullable = false)
    private Integer durationDays; // How long the medicine should continue

    @Column(length = 500)
    private String specialInstructions;

    public PrescriptionItem() {
    }

    public PrescriptionItem(Prescription prescription, Medication medication, Integer quantity,
            String dosageInstructions, Integer durationDays) {
        this.prescription = prescription;
        this.medication = medication;
        this.quantity = quantity;
        this.dosageInstructions = dosageInstructions;
        this.durationDays = durationDays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDosageInstructions() {
        return dosageInstructions;
    }

    public void setDosageInstructions(String dosageInstructions) {
        this.dosageInstructions = dosageInstructions;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public double getItemTotal() {
        return quantity * medication.getUnitPrice();
    }

    public boolean canBeFilled() {
        return medication.getStockQuantity() >= quantity;
    }

    @Override
    public String toString() {
        return medication.getName() + " x" + quantity + " (" + dosageInstructions + ")";
    }
}
