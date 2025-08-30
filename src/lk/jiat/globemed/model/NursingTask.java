package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nursing_tasks")
public class NursingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "assigned_nurse_id", nullable = false)
    private Staff assignedNurse;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private Staff createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dueDateTime;

    private LocalDateTime completedAt;

    @Column(length = 50)
    private String priority; // Low, Medium, High, Critical

    @Column(length = 50)
    private String status; // Pending, In Progress, Completed, Cancelled

    @Column(length = 100)
    private String taskType; // Medication, Vitals, Assessment, Care, Other

    @Column(length = 1000)
    private String completionNotes;

    public NursingTask() {
    }

    public NursingTask(String title, String description, Staff assignedNurse, Staff createdBy) {
        this.title = title;
        this.description = description;
        this.assignedNurse = assignedNurse;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.status = "Pending";
        this.priority = "Medium";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Staff getAssignedNurse() {
        return assignedNurse;
    }

    public void setAssignedNurse(Staff assignedNurse) {
        this.assignedNurse = assignedNurse;
    }

    public Staff getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Staff createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public boolean isOverdue() {
        return dueDateTime != null && LocalDateTime.now().isAfter(dueDateTime)
                && !"Completed".equals(status) && !"Cancelled".equals(status);
    }

    public boolean isHighPriority() {
        return "High".equals(priority) || "Critical".equals(priority);
    }

    public void markAsCompleted(String notes) {
        this.status = "Completed";
        this.completedAt = LocalDateTime.now();
        this.completionNotes = notes;
    }

    public void markAsInProgress() {
        this.status = "In Progress";
    }

    public void cancel(String reason) {
        this.status = "Cancelled";
        this.completionNotes = "Cancelled: " + reason;
    }

    @Override
    public String toString() {
        return title + " (" + status + ") - " + (patient != null ? patient.getFullName() : "General");
    }
}
