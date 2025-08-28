package lk.jiat.globemed.antipattern;

import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import java.time.LocalDateTime;

/**
 * ANTI-PATTERN EXAMPLE: Spaghetti Code
 * 
 * This class demonstrates the Spaghetti Code anti-pattern with:
 * 1. Deeply nested if-else statements
 * 2. Complex control flow that's hard to follow
 * 3. No clear structure or organization
 * 4. Mixed concerns and responsibilities
 * 5. Hard to test and maintain
 * 
 * REFACTORING SOLUTION: Use early returns, extract methods,
 * apply design patterns, and separate concerns.
 */
public class SpaghettiCodeExample {
    
    /**
     * BAD EXAMPLE: Spaghetti code with deep nesting and complex logic
     */
    public String processAppointmentBadWay(Appointment appointment) {
        // This is TERRIBLE code - deeply nested and hard to follow
        if (appointment != null) {
            if (appointment.getPatient() != null) {
                if (appointment.getDoctor() != null) {
                    if (appointment.getAppointmentDateTime() != null) {
                        if (appointment.getStatus() != null) {
                            if (appointment.getStatus().equals("Scheduled")) {
                                if (appointment.getAppointmentDateTime().isAfter(LocalDateTime.now())) {
                                    if (appointment.getPatient().getDob() != null) {
                                        if (appointment.getDoctor().getRole() != null) {
                                            if (appointment.getDoctor().getRole().getName().equals("Doctor")) {
                                                // Finally, some actual logic buried deep inside!
                                                String result = "Processing appointment for " + 
                                                              appointment.getPatient().getFullName();
                                                
                                                // More nested conditions...
                                                if (appointment.getPatient().getGender() != null) {
                                                    if (appointment.getPatient().getGender().equals("Male")) {
                                                        if (calculateAge(appointment.getPatient()) > 65) {
                                                            result += " - Senior male patient";
                                                            if (appointment.getAppointmentDateTime().getHour() < 12) {
                                                                result += " - Morning appointment preferred";
                                                                if (appointment.getDoctor().getName().contains("Dr.")) {
                                                                    result += " - With qualified doctor";
                                                                    // Even more nesting...
                                                                    if (isWeekday(appointment.getAppointmentDateTime())) {
                                                                        result += " - Weekday appointment";
                                                                        if (appointment.getAppointmentDateTime().getMinute() == 0) {
                                                                            result += " - On the hour";
                                                                            // This is getting ridiculous...
                                                                            return result + " - PROCESSED";
                                                                        } else {
                                                                            return result + " - Not on the hour";
                                                                        }
                                                                    } else {
                                                                        return result + " - Weekend appointment";
                                                                    }
                                                                } else {
                                                                    return result + " - Doctor title missing";
                                                                }
                                                            } else {
                                                                result += " - Afternoon appointment";
                                                                return result + " - PROCESSED";
                                                            }
                                                        } else {
                                                            result += " - Adult male patient";
                                                            return result + " - PROCESSED";
                                                        }
                                                    } else if (appointment.getPatient().getGender().equals("Female")) {
                                                        if (calculateAge(appointment.getPatient()) > 65) {
                                                            result += " - Senior female patient";
                                                            // More nested logic...
                                                            return result + " - PROCESSED";
                                                        } else {
                                                            result += " - Adult female patient";
                                                            return result + " - PROCESSED";
                                                        }
                                                    } else {
                                                        result += " - Gender not specified";
                                                        return result + " - PROCESSED";
                                                    }
                                                } else {
                                                    return result + " - No gender information";
                                                }
                                            } else {
                                                return "Error: Staff member is not a doctor";
                                            }
                                        } else {
                                            return "Error: Doctor role not specified";
                                        }
                                    } else {
                                        return "Error: Patient date of birth missing";
                                    }
                                } else {
                                    return "Error: Appointment is in the past";
                                }
                            } else if (appointment.getStatus().equals("Cancelled")) {
                                // Another branch of spaghetti...
                                if (appointment.getPatient() != null) {
                                    if (appointment.getDoctor() != null) {
                                        return "Cancelled appointment for " + 
                                               appointment.getPatient().getFullName() + 
                                               " with " + appointment.getDoctor().getName();
                                    } else {
                                        return "Cancelled appointment - doctor information missing";
                                    }
                                } else {
                                    return "Cancelled appointment - patient information missing";
                                }
                            } else if (appointment.getStatus().equals("Completed")) {
                                // Yet another branch...
                                return "Completed appointment";
                            } else {
                                return "Unknown appointment status: " + appointment.getStatus();
                            }
                        } else {
                            return "Error: Appointment status is null";
                        }
                    } else {
                        return "Error: Appointment date/time is null";
                    }
                } else {
                    return "Error: Doctor is null";
                }
            } else {
                return "Error: Patient is null";
            }
        } else {
            return "Error: Appointment is null";
        }
    }
    
    /**
     * GOOD EXAMPLE: Refactored code with early returns and clear structure
     */
    public String processAppointmentGoodWay(Appointment appointment) {
        // Early validation with clear error messages
        String validationError = validateAppointment(appointment);
        if (validationError != null) {
            return validationError;
        }
        
        // Process based on status using strategy pattern approach
        switch (appointment.getStatus()) {
            case "Scheduled":
                return processScheduledAppointment(appointment);
            case "Cancelled":
                return processCancelledAppointment(appointment);
            case "Completed":
                return processCompletedAppointment(appointment);
            default:
                return "Unknown appointment status: " + appointment.getStatus();
        }
    }
    
    private String validateAppointment(Appointment appointment) {
        if (appointment == null) {
            return "Error: Appointment is null";
        }
        if (appointment.getPatient() == null) {
            return "Error: Patient is null";
        }
        if (appointment.getDoctor() == null) {
            return "Error: Doctor is null";
        }
        if (appointment.getAppointmentDateTime() == null) {
            return "Error: Appointment date/time is null";
        }
        if (appointment.getStatus() == null) {
            return "Error: Appointment status is null";
        }
        if (appointment.getPatient().getDob() == null) {
            return "Error: Patient date of birth missing";
        }
        if (appointment.getDoctor().getRole() == null) {
            return "Error: Doctor role not specified";
        }
        if (!"Doctor".equals(appointment.getDoctor().getRole().getName())) {
            return "Error: Staff member is not a doctor";
        }
        
        return null; // No validation errors
    }
    
    private String processScheduledAppointment(Appointment appointment) {
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            return "Error: Appointment is in the past";
        }
        
        StringBuilder result = new StringBuilder("Processing appointment for ");
        result.append(appointment.getPatient().getFullName());
        
        // Add patient demographic information
        addPatientDemographics(result, appointment.getPatient());
        
        // Add appointment timing information
        addAppointmentTiming(result, appointment);
        
        // Add doctor information
        addDoctorInformation(result, appointment.getDoctor());
        
        return result.append(" - PROCESSED").toString();
    }
    
    private void addPatientDemographics(StringBuilder result, Patient patient) {
        if (patient.getGender() == null) {
            result.append(" - No gender information");
            return;
        }
        
        int age = calculateAge(patient);
        boolean isSenior = age > 65;
        
        if ("Male".equals(patient.getGender())) {
            result.append(isSenior ? " - Senior male patient" : " - Adult male patient");
        } else if ("Female".equals(patient.getGender())) {
            result.append(isSenior ? " - Senior female patient" : " - Adult female patient");
        } else {
            result.append(" - Gender not specified");
        }
    }
    
    private void addAppointmentTiming(StringBuilder result, Appointment appointment) {
        LocalDateTime dateTime = appointment.getAppointmentDateTime();
        
        if (dateTime.getHour() < 12) {
            result.append(" - Morning appointment");
            if (calculateAge(appointment.getPatient()) > 65) {
                result.append(" preferred");
            }
        } else {
            result.append(" - Afternoon appointment");
        }
        
        if (isWeekday(dateTime)) {
            result.append(" - Weekday");
        } else {
            result.append(" - Weekend");
        }
        
        if (dateTime.getMinute() == 0) {
            result.append(" - On the hour");
        }
    }
    
    private void addDoctorInformation(StringBuilder result, Staff doctor) {
        if (doctor.getName().contains("Dr.")) {
            result.append(" - With qualified doctor");
        } else {
            result.append(" - Doctor title missing");
        }
    }
    
    private String processCancelledAppointment(Appointment appointment) {
        return String.format("Cancelled appointment for %s with %s",
                           appointment.getPatient().getFullName(),
                           appointment.getDoctor().getName());
    }
    
    private String processCompletedAppointment(Appointment appointment) {
        return "Completed appointment";
    }
    
    // Helper methods
    private int calculateAge(Patient patient) {
        // Simplified age calculation
        return LocalDateTime.now().getYear() - patient.getDob().getYear();
    }
    
    private boolean isWeekday(LocalDateTime dateTime) {
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        return dayOfWeek >= 1 && dayOfWeek <= 5; // Monday to Friday
    }
    
    /*
     * REFACTORING PRINCIPLES APPLIED:
     * 
     * 1. Early Returns: Validate inputs early and return immediately on error
     * 2. Extract Methods: Break complex logic into smaller, focused methods
     * 3. Single Responsibility: Each method has one clear purpose
     * 4. Strategy Pattern: Use switch/case or polymorphism instead of nested if-else
     * 5. Guard Clauses: Use guard clauses to handle edge cases early
     * 6. Meaningful Names: Use descriptive method and variable names
     * 7. Consistent Structure: Follow a consistent pattern throughout
     * 8. Separation of Concerns: Separate validation, processing, and formatting
     */
}