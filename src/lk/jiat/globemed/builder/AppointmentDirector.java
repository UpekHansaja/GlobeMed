package lk.jiat.globemed.builder;

import java.time.LocalDateTime;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;

public class AppointmentDirector {

    private AppointmentBuilder builder;

    public AppointmentDirector() {
        this.builder = new AppointmentBuilder();
    }

    public AppointmentDirector(AppointmentBuilder builder) {
        this.builder = builder;
    }

    public Appointment createEmergencyAppointment(Patient patient, Staff doctor) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .asEmergency()
                .build();
    }

    public Appointment createRoutineAppointment(Patient patient, Staff doctor, LocalDateTime dateTime) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(dateTime)
                .asRoutineCheckup()
                .build();
    }

    public Appointment createUrgentAppointment(Patient patient, Staff doctor) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withUrgency(true)
                .build();
    }

    public Appointment createFollowUpAppointment(Patient patient, Staff doctor, LocalDateTime dateTime) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(dateTime)
                .asFollowUp()
                .build();
    }

    public Appointment createScheduledAppointment(Patient patient, Staff doctor, LocalDateTime dateTime, String status) {
        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(dateTime)
                .withStatus(status)
                .build();
    }

    public Appointment createNextAvailableAppointment(Patient patient, Staff doctor) {
        LocalDateTime nextSlot = getNextAvailableSlot();

        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(nextSlot)
                .withStatus("Scheduled")
                .build();
    }

    public Appointment createRescheduledAppointment(Appointment originalAppointment, LocalDateTime newDateTime) {
        return builder.reset()
                .withId(originalAppointment.getId())
                .withPatient(originalAppointment.getPatient())
                .withDoctor(originalAppointment.getDoctor())
                .withDateTime(newDateTime)
                .withStatus("Rescheduled")
                .build();
    }

    public Appointment createWalkInAppointment(Patient patient, Staff doctor) {
        LocalDateTime walkInTime = LocalDateTime.now().plusMinutes(15);

        return builder.reset()
                .withPatient(patient)
                .withDoctor(doctor)
                .withDateTime(walkInTime)
                .withStatus("Walk-in")
                .build();
    }

    private LocalDateTime getNextAvailableSlot() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSlot = now.plusDays(1);

        nextSlot = nextSlot.withHour(9).withMinute(0).withSecond(0).withNano(0);

        // Skip weekends
        while (nextSlot.getDayOfWeek().getValue() == 6 || nextSlot.getDayOfWeek().getValue() == 7) {
            nextSlot = nextSlot.plusDays(1);
        }

        return nextSlot;
    }

    public void setBuilder(AppointmentBuilder builder) {
        this.builder = builder;
    }

    public AppointmentBuilder getBuilder() {
        return builder;
    }
}
