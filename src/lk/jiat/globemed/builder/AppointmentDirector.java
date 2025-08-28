package lk.jiat.globemed.builder;

import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import java.time.LocalDateTime;

/**
 * Director class for Builder Pattern
 * Provides pre-configured appointment creation methods
 */
public class AppointmentDirector {
    private AppointmentBuilder builder;
    
    public AppointmentDirector() {
        this.builder = new AppointmentBuilder();
    }
    
    public AppointmentDirector(AppointmentBuilder builder) {
        this.builder = builder;
    }
    
    /**
     * Creates an emergency appointment (immediate scheduling)
     */
    public Appointment createEmergencyAppointment(Patient patient, Staff doctor) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .asEmergency()
                .build();
    }
    
    /**
     * Creates a routine checkup appointment
     */
    public Appointment createRoutineAppointment(Patient patient, Staff doctor, LocalDateTime dateTime) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(dateTime)
                .asRoutineCheckup()
                .build();
    }
    
    /**
     * Creates an urgent appointment (within 2 hours)
     */
    public Appointment createUrgentAppointment(Patient patient, Staff doctor) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withUrgency(true)
                .build();
    }
    
    /**
     * Creates a follow-up appointment
     */
    public Appointment createFollowUpAppointment(Patient patient, Staff doctor, LocalDateTime dateTime) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(dateTime)
                .asFollowUp()
                .build();
    }
    
    /**
     * Creates a scheduled appointment with specific date and time
     */
    public Appointment createScheduledAppointment(Patient patient, Staff doctor, LocalDateTime dateTime, String status) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(dateTime)
                .withStatus(status)
                .build();
    }
    
    /**
     * Creates an appointment for next available slot (next business day at 9 AM)
     */
    public Appointment createNextAvailableAppointment(Patient patient, Staff doctor) {
        LocalDateTime nextSlot = getNextAvailableSlot();
        
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(nextSlot)
                .withStatus("Scheduled")
                .build();
    }
    
    /**
     * Creates a rescheduled appointment
     */
    public Appointment createRescheduledAppointment(Appointment originalAppointment, LocalDateTime newDateTime) {
        return builder.reset()
                .withId(originalAppointment.getId())
                .withPatient(originalAppointment.getPatient())
                .withDoctor(originalAppointment.getDoctor())
                .withDateTime(newDateTime)
                .withStatus("Rescheduled")
                .build();
    }
    
    /**
     * Creates a walk-in appointment (immediate but not emergency)
     */
    public Appointment createWalkInAppointment(Patient patient, Staff doctor) {
        LocalDateTime walkInTime = LocalDateTime.now().plusMinutes(15); // 15 minutes from now
        
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(walkInTime)
                .withStatus("Walk-in")
                .build();
    }
    
    private LocalDateTime getNextAvailableSlot() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSlot = now.plusDays(1); // Start with tomorrow
        
        // Set to 9 AM
        nextSlot = nextSlot.withHour(9).withMinute(0).withSecond(0).withNano(0);
        
        // Skip weekends
        while (nextSlot.getDayOfWeek().getValue() == 6 || nextSlot.getDayOfWeek().getValue() == 7) {
            nextSlot = nextSlot.plusDays(1);
        }
        
        return nextSlot;
    }
    
    // Setter for builder (allows different builders to be used)
    public void setBuilder(AppointmentBuilder builder) {
        this.builder = builder;
    }
    
    public AppointmentBuilder getBuilder() {
        return builder;
    }
}