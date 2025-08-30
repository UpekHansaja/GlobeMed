package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_vitals")
public class PatientVitals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "nurse_id", nullable = false)
    private Staff nurse;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    private Double temperature; // in Celsius
    private Integer systolicBP; // Systolic Blood Pressure
    private Integer diastolicBP; // Diastolic Blood Pressure
    private Integer heartRate; // beats per minute
    private Integer respiratoryRate; // breaths per minute
    private Double oxygenSaturation; // percentage
    private Double weight; // in kg
    private Double height; // in cm

    @Column(length = 500)
    private String notes;

    public PatientVitals() {
    }

    public PatientVitals(Patient patient, Staff nurse) {
        this.patient = patient;
        this.nurse = nurse;
        this.recordedAt = LocalDateTime.now();
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

    public Staff getNurse() {
        return nurse;
    }

    public void setNurse(Staff nurse) {
        this.nurse = nurse;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(Integer systolicBP) {
        this.systolicBP = systolicBP;
    }

    public Integer getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(Integer diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public Double getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(Double oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBloodPressure() {
        if (systolicBP != null && diastolicBP != null) {
            return systolicBP + "/" + diastolicBP;
        }
        return "N/A";
    }

    public boolean isTemperatureFever() {
        return temperature != null && temperature > 37.5;
    }

    public boolean isBloodPressureHigh() {
        return systolicBP != null && diastolicBP != null
                && (systolicBP > 140 || diastolicBP > 90);
    }

    public boolean isHeartRateAbnormal() {
        return heartRate != null && (heartRate < 60 || heartRate > 100);
    }

    @Override
    public String toString() {
        return "Vitals for " + patient.getFullName() + " at " + recordedAt;
    }
}
