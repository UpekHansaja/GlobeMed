package lk.jiat.globemed.model;

import java.time.LocalDate;

/**
 * Flyweight class for managing appointment time slots
 * Intrinsic state: time and duration (shared)
 * Extrinsic state: doctor, patient, date (passed as parameters)
 */
public class TimeSlot {
    // Intrinsic state (shared among all instances)
    private final String time; // e.g., "09:00", "10:30"
    private final int duration; // in minutes
    private final String timeSlotType; // "CONSULTATION", "SURGERY", "CHECKUP"
    
    public TimeSlot(String time, int duration, String timeSlotType) {
        this.time = time;
        this.duration = duration;
        this.timeSlotType = timeSlotType;
        
        // Simulate heavy object creation cost
        System.out.println("🏗️ Creating TimeSlot flyweight: " + time + " (" + duration + " min, " + timeSlotType + ")");
    }
    
    /**
     * Operation that uses both intrinsic and extrinsic state
     */
    public void scheduleAppointment(String doctorName, String patientName, LocalDate date, String status) {
        // Extrinsic state is passed as parameters
        System.out.println(String.format("📅 Scheduled: %s with Dr. %s on %s at %s (%d min %s) - Status: %s",
                patientName, doctorName, date, time, duration, timeSlotType, status));
    }
    
    /**
     * Check if this time slot is suitable for a specific appointment type
     */
    public boolean isSuitableFor(String appointmentType, int requiredDuration) {
        // Use intrinsic state to determine suitability
        if (requiredDuration > duration) {
            return false;
        }
        
        switch (timeSlotType.toUpperCase()) {
            case "CONSULTATION":
                return appointmentType.equalsIgnoreCase("consultation") || 
                       appointmentType.equalsIgnoreCase("checkup") ||
                       appointmentType.equalsIgnoreCase("follow-up");
                       
            case "SURGERY":
                return appointmentType.equalsIgnoreCase("surgery") ||
                       appointmentType.equalsIgnoreCase("procedure");
                       
            case "CHECKUP":
                return appointmentType.equalsIgnoreCase("checkup") ||
                       appointmentType.equalsIgnoreCase("routine") ||
                       appointmentType.equalsIgnoreCase("screening");
                       
            default:
                return true; // Generic slots can handle any type
        }
    }
    
    /**
     * Display time slot information with context
     */
    public void displaySlotInfo(String context) {
        System.out.println(String.format("⏰ TimeSlot [%s]: %s (%d min) - %s", 
                context, time, duration, timeSlotType));
    }
    
    /**
     * Calculate cost based on time slot type and duration
     */
    public double calculateCost(String patientType, boolean hasInsurance) {
        // Base cost calculation using intrinsic state
        double baseCost = 0;
        
        switch (timeSlotType.toUpperCase()) {
            case "CONSULTATION":
                baseCost = 100.0;
                break;
            case "SURGERY":
                baseCost = 500.0;
                break;
            case "CHECKUP":
                baseCost = 75.0;
                break;
            default:
                baseCost = 50.0;
        }
        
        // Adjust based on duration
        baseCost = baseCost * (duration / 30.0); // Base 30-minute rate
        
        // Apply extrinsic state modifiers
        if ("VIP".equalsIgnoreCase(patientType)) {
            baseCost *= 1.5;
        }
        
        if (hasInsurance) {
            baseCost *= 0.8; // 20% discount for insured patients
        }
        
        return baseCost;
    }
    
    // Getters for intrinsic state
    public String getTime() {
        return time;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public String getTimeSlotType() {
        return timeSlotType;
    }
    
    @Override
    public String toString() {
        return String.format("TimeSlot{time='%s', duration=%d, type='%s'}", 
                time, duration, timeSlotType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TimeSlot timeSlot = (TimeSlot) obj;
        return duration == timeSlot.duration &&
               time.equals(timeSlot.time) &&
               timeSlotType.equals(timeSlot.timeSlotType);
    }
    
    @Override
    public int hashCode() {
        int result = time.hashCode();
        result = 31 * result + duration;
        result = 31 * result + timeSlotType.hashCode();
        return result;
    }
}