package lk.jiat.globemed.service;

import lk.jiat.globemed.dao.PatientVitalsDao;
import lk.jiat.globemed.dao.NursingTaskDao;
import lk.jiat.globemed.dao.AppointmentDao;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.model.PatientVitals;
import lk.jiat.globemed.model.NursingTask;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for nurse-specific operations
 * 
 * @author upekhansaja
 */
public class NurseService {
    
    private final PatientVitalsDao vitalsDao = new PatientVitalsDao();
    private final NursingTaskDao taskDao = new NursingTaskDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final PatientDao patientDao = new PatientDao();
    
    // ===== PATIENT VITALS MANAGEMENT =====
    
    /**
     * Record patient vitals
     */
    public PatientVitals recordPatientVitals(Patient patient, Staff nurse, 
            Double temperature, Integer systolicBP, Integer diastolicBP, 
            Integer heartRate, Integer respiratoryRate, Double oxygenSaturation,
            Double weight, Double height, String notes) {
        
        PatientVitals vitals = new PatientVitals(patient, nurse);
        vitals.setTemperature(temperature);
        vitals.setSystolicBP(systolicBP);
        vitals.setDiastolicBP(diastolicBP);
        vitals.setHeartRate(heartRate);
        vitals.setRespiratoryRate(respiratoryRate);
        vitals.setOxygenSaturation(oxygenSaturation);
        vitals.setWeight(weight);
        vitals.setHeight(height);
        vitals.setNotes(notes);
        
        return vitalsDao.create(vitals);
    }
    
    /**
     * Get patient's vital history
     */
    public List<PatientVitals> getPatientVitalHistory(Long patientId) {
        return vitalsDao.findByPatientId(patientId);
    }
    
    /**
     * Get latest vitals for a patient
     */
    public PatientVitals getLatestVitals(Long patientId) {
        return vitalsDao.getLatestVitalsForPatient(patientId);
    }
    
    /**
     * Get all abnormal vitals
     */
    public List<PatientVitals> getAbnormalVitals() {
        return vitalsDao.findAbnormalVitals();
    }
    
    /**
     * Get vitals recorded by a specific nurse
     */
    public List<PatientVitals> getVitalsByNurse(Long nurseId) {
        return vitalsDao.findByNurseId(nurseId);
    }
    
    // ===== NURSING TASK MANAGEMENT =====
    
    /**
     * Create a new nursing task
     */
    public NursingTask createTask(String title, String description, Patient patient,
            Staff assignedNurse, Staff createdBy, String priority, String taskType,
            LocalDateTime dueDateTime) {
        
        NursingTask task = new NursingTask(title, description, assignedNurse, createdBy);
        task.setPatient(patient);
        task.setPriority(priority);
        task.setTaskType(taskType);
        task.setDueDateTime(dueDateTime);
        
        return taskDao.create(task);
    }
    
    /**
     * Get tasks assigned to a nurse
     */
    public List<NursingTask> getTasksForNurse(Long nurseId) {
        return taskDao.findByAssignedNurseId(nurseId);
    }
    
    /**
     * Get pending tasks for a nurse
     */
    public List<NursingTask> getPendingTasksForNurse(Long nurseId) {
        return taskDao.findTasksByNurseAndStatus(nurseId, "Pending");
    }
    
    /**
     * Get tasks due today for a nurse
     */
    public List<NursingTask> getTasksDueToday(Long nurseId) {
        return taskDao.findTasksDueToday(nurseId);
    }
    
    /**
     * Complete a task
     */
    public void completeTask(Long taskId, String completionNotes) {
        NursingTask task = taskDao.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        
        task.markAsCompleted(completionNotes);
        taskDao.update(task);
        
        System.out.println("✅ Task completed: " + task.getTitle());
    }
    
    /**
     * Start working on a task
     */
    public void startTask(Long taskId) {
        NursingTask task = taskDao.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        
        task.markAsInProgress();
        taskDao.update(task);
        
        System.out.println("🔄 Task started: " + task.getTitle());
    }
    
    /**
     * Cancel a task
     */
    public void cancelTask(Long taskId, String reason) {
        NursingTask task = taskDao.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        
        task.cancel(reason);
        taskDao.update(task);
        
        System.out.println("❌ Task cancelled: " + task.getTitle());
    }
    
    /**
     * Get overdue tasks
     */
    public List<NursingTask> getOverdueTasks() {
        return taskDao.findOverdueTasks();
    }
    
    /**
     * Get high priority tasks
     */
    public List<NursingTask> getHighPriorityTasks() {
        return taskDao.findHighPriorityTasks();
    }
    
    // ===== APPOINTMENT MANAGEMENT =====
    
    /**
     * Get today's appointments
     */
    public List<Appointment> getTodaysAppointments() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return appointmentDao.findBetween(startOfDay, endOfDay);
    }
    
    /**
     * Get scheduled appointments
     */
    public List<Appointment> getScheduledAppointments() {
        return appointmentDao.findByStatus("Scheduled");
    }
    
    /**
     * Update appointment status
     */
    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentDao.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }
        
        appointment.setStatus(status);
        appointmentDao.update(appointment);
        
        System.out.println("📅 Appointment status updated: " + status);
    }
    
    // ===== PATIENT MANAGEMENT =====
    
    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        return patientDao.findAll();
    }
    
    /**
     * Search patients by name
     */
    public List<Patient> searchPatients(String searchTerm) {
        return patientDao.findAll().stream()
            .filter(p -> p.getFullName().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    // ===== STATISTICS AND REPORTING =====
    
    /**
     * Get nursing statistics
     */
    public Map<String, Object> getNursingStatistics(Long nurseId) {
        List<NursingTask> allTasks = getTasksForNurse(nurseId);
        List<PatientVitals> allVitals = getVitalsByNurse(nurseId);
        
        long pendingTasks = taskDao.countTasksByNurseAndStatus(nurseId, "Pending");
        long inProgressTasks = taskDao.countTasksByNurseAndStatus(nurseId, "In Progress");
        long completedTasks = taskDao.countTasksByNurseAndStatus(nurseId, "Completed");
        
        List<NursingTask> overdueTasks = allTasks.stream()
            .filter(NursingTask::isOverdue)
            .collect(Collectors.toList());
        
        List<NursingTask> highPriorityTasks = allTasks.stream()
            .filter(NursingTask::isHighPriority)
            .collect(Collectors.toList());
        
        return Map.of(
            "totalTasks", allTasks.size(),
            "pendingTasks", pendingTasks,
            "inProgressTasks", inProgressTasks,
            "completedTasks", completedTasks,
            "overdueTasks", overdueTasks.size(),
            "highPriorityTasks", highPriorityTasks.size(),
            "vitalsRecorded", allVitals.size(),
            "tasksDueToday", getTasksDueToday(nurseId).size()
        );
    }
    
    /**
     * Generate nursing report
     */
    public String generateNursingReport(Long nurseId, Staff nurse) {
        Map<String, Object> stats = getNursingStatistics(nurseId);
        List<NursingTask> recentTasks = getTasksForNurse(nurseId).stream()
            .limit(10)
            .collect(Collectors.toList());
        
        StringBuilder report = new StringBuilder();
        report.append("=== NURSING REPORT ===\n");
        report.append("Nurse: ").append(nurse.getName()).append("\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        report.append("📊 TASK SUMMARY:\n");
        report.append("Total Tasks: ").append(stats.get("totalTasks")).append("\n");
        report.append("Pending: ").append(stats.get("pendingTasks")).append("\n");
        report.append("In Progress: ").append(stats.get("inProgressTasks")).append("\n");
        report.append("Completed: ").append(stats.get("completedTasks")).append("\n");
        report.append("Overdue: ").append(stats.get("overdueTasks")).append("\n");
        report.append("High Priority: ").append(stats.get("highPriorityTasks")).append("\n");
        report.append("Due Today: ").append(stats.get("tasksDueToday")).append("\n\n");
        
        report.append("🩺 VITALS RECORDED: ").append(stats.get("vitalsRecorded")).append("\n\n");
        
        report.append("📋 RECENT TASKS:\n");
        for (NursingTask task : recentTasks) {
            report.append("- ").append(task.getTitle())
                  .append(" (").append(task.getStatus()).append(")")
                  .append(" - ").append(task.getPriority()).append(" Priority")
                  .append(" - ").append(task.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                  .append("\n");
        }
        
        report.append("\n=== END OF REPORT ===");
        return report.toString();
    }
    
    /**
     * Generate patient vitals report
     */
    public String generateVitalsReport(Long patientId) {
        Patient patient = patientDao.findById(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }
        
        List<PatientVitals> vitals = getPatientVitalHistory(patientId);
        
        StringBuilder report = new StringBuilder();
        report.append("=== PATIENT VITALS REPORT ===\n");
        report.append("Patient: ").append(patient.getFullName()).append("\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        if (vitals.isEmpty()) {
            report.append("No vitals recorded for this patient.\n");
        } else {
            report.append("📊 VITALS HISTORY (").append(vitals.size()).append(" records):\n\n");
            
            for (PatientVitals vital : vitals) {
                report.append("Date: ").append(vital.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
                report.append("Nurse: ").append(vital.getNurse().getName()).append("\n");
                
                if (vital.getTemperature() != null) {
                    report.append("Temperature: ").append(vital.getTemperature()).append("°C");
                    if (vital.isTemperatureFever()) report.append(" ⚠️ FEVER");
                    report.append("\n");
                }
                
                if (vital.getSystolicBP() != null && vital.getDiastolicBP() != null) {
                    report.append("Blood Pressure: ").append(vital.getBloodPressure()).append(" mmHg");
                    if (vital.isBloodPressureHigh()) report.append(" ⚠️ HIGH");
                    report.append("\n");
                }
                
                if (vital.getHeartRate() != null) {
                    report.append("Heart Rate: ").append(vital.getHeartRate()).append(" bpm");
                    if (vital.isHeartRateAbnormal()) report.append(" ⚠️ ABNORMAL");
                    report.append("\n");
                }
                
                if (vital.getRespiratoryRate() != null) {
                    report.append("Respiratory Rate: ").append(vital.getRespiratoryRate()).append(" breaths/min\n");
                }
                
                if (vital.getOxygenSaturation() != null) {
                    report.append("Oxygen Saturation: ").append(vital.getOxygenSaturation()).append("%");
                    if (vital.getOxygenSaturation() < 95) report.append(" ⚠️ LOW");
                    report.append("\n");
                }
                
                if (vital.getWeight() != null) {
                    report.append("Weight: ").append(vital.getWeight()).append(" kg\n");
                }
                
                if (vital.getHeight() != null) {
                    report.append("Height: ").append(vital.getHeight()).append(" cm\n");
                }
                
                if (vital.getNotes() != null && !vital.getNotes().trim().isEmpty()) {
                    report.append("Notes: ").append(vital.getNotes()).append("\n");
                }
                
                report.append("\n");
            }
        }
        
        report.append("=== END OF REPORT ===");
        return report.toString();
    }
}