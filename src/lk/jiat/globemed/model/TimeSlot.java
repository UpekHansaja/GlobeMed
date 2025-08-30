package lk.jiat.globemed.model;

import java.time.LocalDate;

public class TimeSlot {

    private final String time;
    private final int duration;
    private final String timeSlotType; // Consultaion, Checkup, Surgery

    public TimeSlot(String time, int duration, String timeSlotType) {
        this.time = time;
        this.duration = duration;
        this.timeSlotType = timeSlotType;

        System.out.println("🏗️ Creating TimeSlot flyweight: " + time + " (" + duration + " min, " + timeSlotType + ")");
    }

    public void scheduleAppointment(String doctorName, String patientName, LocalDate date, String status) {

        System.out.println(String.format("📅 Scheduled: %s with Dr. %s on %s at %s (%d min %s) - Status: %s",
                patientName, doctorName, date, time, duration, timeSlotType, status));
    }

    public boolean isSuitableFor(String appointmentType, int requiredDuration) {

        if (requiredDuration > duration) {
            return false;
        }

        switch (timeSlotType.toUpperCase()) {
            case "CONSULTATION":
                return appointmentType.equalsIgnoreCase("consultation")
                        || appointmentType.equalsIgnoreCase("checkup")
                        || appointmentType.equalsIgnoreCase("follow-up");

            case "SURGERY":
                return appointmentType.equalsIgnoreCase("surgery")
                        || appointmentType.equalsIgnoreCase("procedure");

            case "CHECKUP":
                return appointmentType.equalsIgnoreCase("checkup")
                        || appointmentType.equalsIgnoreCase("routine")
                        || appointmentType.equalsIgnoreCase("screening");

            default:
                return true;
        }
    }

    public void displaySlotInfo(String context) {
        System.out.println(String.format("⏰ TimeSlot [%s]: %s (%d min) - %s",
                context, time, duration, timeSlotType));
    }

    public double calculateCost(String patientType, boolean hasInsurance) {

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

        baseCost = baseCost * (duration / 30.0);

        if ("VIP".equalsIgnoreCase(patientType)) {
            baseCost *= 1.5;
        }

        if (hasInsurance) {
            baseCost *= 0.8; // 20% discount for insured patients
        }

        return baseCost;
    }

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
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TimeSlot timeSlot = (TimeSlot) obj;
        return duration == timeSlot.duration
                && time.equals(timeSlot.time)
                && timeSlotType.equals(timeSlot.timeSlotType);
    }

    @Override
    public int hashCode() {
        int result = time.hashCode();
        result = 31 * result + duration;
        result = 31 * result + timeSlotType.hashCode();
        return result;
    }
}
