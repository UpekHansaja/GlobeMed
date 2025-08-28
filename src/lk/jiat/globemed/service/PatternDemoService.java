package lk.jiat.globemed.service;

import lk.jiat.globemed.builder.AppointmentBuilder;
import lk.jiat.globemed.builder.AppointmentDirector;
import lk.jiat.globemed.interpreter.*;
import lk.jiat.globemed.model.*;
import lk.jiat.globemed.prototype.AppointmentPrototypeRegistry;
import lk.jiat.globemed.prototype.AppointmentPrototype;
import lk.jiat.globemed.service.approval.*;
import lk.jiat.globemed.service.notification.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class demonstrating all advanced design patterns working together
 * This showcases the integration of multiple patterns in a real-world scenario
 */
public class PatternDemoService {
    
    // Pattern instances
    private AppointmentDirector appointmentDirector;
    private AppointmentPrototypeRegistry prototypeRegistry;
    private ApprovalHandler approvalChain;
    private Hospital mainHospital;
    
    public PatternDemoService() {
        initializePatterns();
    }
    
    private void initializePatterns() {
        // Initialize Builder Pattern
        appointmentDirector = new AppointmentDirector();
        
        // Initialize Prototype Pattern
        prototypeRegistry = AppointmentPrototypeRegistry.getInstance();
        
        // Initialize Chain of Responsibility Pattern
        approvalChain = ApprovalChainBuilder.createStandardChain();
        
        // Initialize Composite Pattern
        initializeHospitalStructure();
        
        // Initialize Flyweight Pattern
        TimeSlotFactory.initializeStandardSlots();
        
        System.out.println("🎯 All design patterns initialized successfully!");
    }
    
    private void initializeHospitalStructure() {
        // Create main hospital (Composite)
        mainHospital = new Hospital("GlobeMed Medical Center", 
                                   "Advanced healthcare facility", 
                                   "GMC001", 
                                   "123 Healthcare Boulevard");
        
        // Create departments (Leaf nodes)
        Department cardiology = new Department("Cardiology Department", 
                                             "Heart and cardiovascular care", 
                                             "CARD", 150000.0);
        
        Department emergency = new Department("Emergency Department", 
                                            "24/7 emergency medical services", 
                                            "EMER", 200000.0);
        
        Department surgery = new Department("Surgery Department", 
                                          "Surgical procedures and operations", 
                                          "SURG", 300000.0);
        
        // Add departments to hospital
        mainHospital.add(cardiology);
        mainHospital.add(emergency);
        mainHospital.add(surgery);
    }
    
    /**
     * Comprehensive demo showing all patterns working together
     */
    public void runComprehensiveDemo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎭 COMPREHENSIVE DESIGN PATTERNS DEMONSTRATION");
        System.out.println("=".repeat(60));
        
        // Create sample data
        Patient patient = createSamplePatient();
        Staff doctor = createSampleDoctor();
        
        // 1. Composite Pattern Demo
        demonstrateCompositePattern();
        
        // 2. Prototype Pattern Demo
        demonstratePrototypePattern();
        
        // 3. Builder Pattern Demo
        Appointment appointment = demonstrateBuilderPattern(patient, doctor);
        
        // 4. Bridge Pattern Demo
        demonstrateBridgePattern(appointment);
        
        // 5. Chain of Responsibility Demo
        demonstrateChainOfResponsibility(appointment);
        
        // 6. Flyweight Pattern Demo
        demonstrateFlyweightPattern();
        
        // 7. Interpreter Pattern Demo
        demonstrateInterpreterPattern(patient, appointment);
        
        // 8. Integration Demo - All patterns working together
        demonstratePatternIntegration(patient, doctor);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ COMPREHENSIVE DEMONSTRATION COMPLETED");
        System.out.println("=".repeat(60));
    }
    
    private void demonstrateCompositePattern() {
        System.out.println("\n🏥 COMPOSITE PATTERN DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        mainHospital.displayInfo();
        
        // Demonstrate finding units
        MedicalUnit cardiology = mainHospital.findUnitByName("Cardiology Department");
        if (cardiology != null) {
            System.out.println("\n🔍 Found unit: " + cardiology.getName());
        }
    }
    
    private void demonstratePrototypePattern() {
        System.out.println("\n📋 PROTOTYPE PATTERN DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        // Show available prototypes
        System.out.println("Available prototypes: " + prototypeRegistry.getAvailableTypes());
        
        // Clone different appointment types
        AppointmentPrototype routine = prototypeRegistry.getPrototype("routine");
        AppointmentPrototype emergency = prototypeRegistry.getPrototype("emergency");
        AppointmentPrototype surgery = prototypeRegistry.getPrototype("surgery");
        
        routine.displayInfo();
        
        // Create multiple instances
        AppointmentPrototype[] consultations = prototypeRegistry.createMultiple("consultation", 3);
        System.out.println("Created " + consultations.length + " consultation appointments");
    }
    
    private Appointment demonstrateBuilderPattern(Patient patient, Staff doctor) {
        System.out.println("\n🔨 BUILDER PATTERN DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        // Use director for common scenarios
        Appointment emergency = appointmentDirector.createEmergencyAppointment(patient, doctor);
        System.out.println("Emergency appointment: " + emergency.getStatus() + 
                          " at " + emergency.getAppointmentDateTime());
        
        // Use builder directly for custom appointments
        AppointmentBuilder builder = new AppointmentBuilder();
        Appointment custom = builder
            .withPatient(patient)
            .withDoctor(doctor)
            .withDateTime(LocalDateTime.now().plusDays(5).withHour(14).withMinute(0))
            .withStatus("Consultation")
            .build();
        
        System.out.println("Custom appointment: " + custom.getStatus() + 
                          " at " + custom.getAppointmentDateTime());
        
        return custom;
    }
    
    private void demonstrateBridgePattern(Appointment appointment) {
        System.out.println("\n🌉 BRIDGE PATTERN DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        // Create different notification senders
        EmailSender emailSender = new EmailSender("smtp.globemed.lk", 587, 
                                                 "system@globemed.lk", "password");
        SMSSender smsSender = new SMSSender("api_key", "https://sms.api.lk", "+94771234567");
        
        // Create notifications with different senders
        AppointmentNotification emailNotif = new AppointmentNotification(
            emailSender, appointment, "SCHEDULED");
        AppointmentNotification smsNotif = new AppointmentNotification(
            smsSender, appointment, "REMINDER");
        
        // Send notifications
        emailNotif.notify("patient@email.com");
        smsNotif.notify("+94771234567");
    }
    
    private void demonstrateChainOfResponsibility(Appointment appointment) {
        System.out.println("\n⛓️ CHAIN OF RESPONSIBILITY DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        // Create different approval requests
        ApprovalRequest lowCostRequest = new ApprovalRequest(
            "APPOINTMENT", 75.0, "nurse@globemed.lk", 
            "Routine checkup appointment", appointment);
        
        ApprovalRequest mediumCostRequest = new ApprovalRequest(
            "SURGERY", 2500.0, "doctor@globemed.lk", 
            "Minor surgical procedure", appointment);
        
        ApprovalRequest highCostRequest = new ApprovalRequest(
            "SURGERY", 15000.0, "admin@globemed.lk", 
            "Major surgical procedure", appointment);
        
        // Process requests through chain
        approvalChain.handleRequest(lowCostRequest);
        approvalChain.handleRequest(mediumCostRequest);
        approvalChain.handleRequest(highCostRequest);
    }
    
    private void demonstrateFlyweightPattern() {
        System.out.println("\n🪶 FLYWEIGHT PATTERN DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        // Request multiple time slots (demonstrating flyweight reuse)
        TimeSlot morning1 = TimeSlotFactory.getConsultationSlot("09:00", 30);
        TimeSlot morning2 = TimeSlotFactory.getConsultationSlot("09:00", 30); // Same as morning1
        TimeSlot surgery1 = TimeSlotFactory.getSurgerySlot("14:00", 120);
        
        // Use flyweights with different extrinsic state
        morning1.scheduleAppointment("Dr. Smith", "John Doe", LocalDate.now(), "Scheduled");
        morning2.scheduleAppointment("Dr. Johnson", "Jane Smith", LocalDate.now().plusDays(1), "Confirmed");
        surgery1.scheduleAppointment("Dr. Wilson", "Bob Brown", LocalDate.now().plusDays(2), "Scheduled");
        
        // Show statistics
        TimeSlotFactory.printStatistics();
    }
    
    private void demonstrateInterpreterPattern(Patient patient, Appointment appointment) {
        System.out.println("\n🔍 INTERPRETER PATTERN DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        // Create query context
        QueryContext context = new QueryContext();
        context.setVariable("patient", patient);
        context.setVariable("appointment", appointment);
        
        // Build complex queries
        Expression seniorMaleQuery = new AndExpression(
            new PatientAgeExpression(">=", 65),
            new PatientGenderExpression("Male")
        );
        
        Expression scheduledOrUrgentQuery = new OrExpression(
            new AppointmentStatusExpression("Scheduled"),
            new AppointmentStatusExpression("URGENT")
        );
        
        Expression complexQuery = new AndExpression(seniorMaleQuery, scheduledOrUrgentQuery);
        
        // Evaluate queries
        System.out.println("Query: " + seniorMaleQuery.toString());
        System.out.println("Result: " + seniorMaleQuery.interpret(context));
        
        System.out.println("Query: " + scheduledOrUrgentQuery.toString());
        System.out.println("Result: " + scheduledOrUrgentQuery.interpret(context));
        
        System.out.println("Complex Query: " + complexQuery.toString());
        System.out.println("Result: " + complexQuery.interpret(context));
        
        // Use QueryBuilder for easier query construction
        QueryBuilder builder = new QueryBuilder();
        Expression builtQuery = builder
            .patientAge(">=", 18)
            .patientGender("Female")
            .appointmentStatus("Scheduled")
            .build();
        
        System.out.println("Built Query: " + builtQuery.toString());
        System.out.println("Result: " + builtQuery.interpret(context));
    }
    
    private void demonstratePatternIntegration(Patient patient, Staff doctor) {
        System.out.println("\n🔗 PATTERN INTEGRATION DEMONSTRATION");
        System.out.println("-".repeat(40));
        
        System.out.println("Creating a complete appointment workflow using all patterns...");
        
        // 1. Use Prototype to get appointment template
        AppointmentPrototype template = prototypeRegistry.getPrototype("consultation");
        System.out.println("📋 Used prototype for: " + template.getType());
        
        // 2. Use Builder to create appointment with template data
        AppointmentBuilder builder = new AppointmentBuilder();
        Appointment appointment = builder
            .withPatient(patient)
            .withDoctor(doctor)
            .withDateTime(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0))
            .withStatus("Consultation")
            .build();
        
        // 3. Use Flyweight for time slot management
        TimeSlot timeSlot = TimeSlotFactory.getConsultationSlot("10:00", 45);
        timeSlot.scheduleAppointment(doctor.getName(), patient.getFullName(), 
                                   appointment.getAppointmentDateTime().toLocalDate(), 
                                   appointment.getStatus());
        
        // 4. Use Chain of Responsibility for approval
        ApprovalRequest request = new ApprovalRequest(
            "APPOINTMENT", template.getEstimatedCost(), doctor.getEmail(),
            "Consultation appointment created from prototype", appointment);
        approvalChain.handleRequest(request);
        
        // 5. Use Bridge for notifications
        if (request.isApproved()) {
            EmailSender emailSender = new EmailSender("smtp.globemed.lk", 587, 
                                                     "system@globemed.lk", "password");
            AppointmentNotification notification = new AppointmentNotification(
                emailSender, appointment, "SCHEDULED");
            notification.notify(patient.getFirstName() + "@email.com");
        }
        
        // 6. Use Interpreter to validate appointment
        QueryContext context = new QueryContext();
        context.setVariable("patient", patient);
        context.setVariable("appointment", appointment);
        
        Expression validationQuery = new AndExpression(
            new PatientAgeExpression(">=", 0), // Valid age
            new AppointmentStatusExpression("Consultation") // Correct status
        );
        
        boolean isValid = validationQuery.interpret(context);
        System.out.println("✅ Appointment validation: " + (isValid ? "PASSED" : "FAILED"));
        
        // 7. Update hospital statistics (Composite)
        System.out.println("🏥 Hospital total budget: $" + mainHospital.getTotalBudget());
        System.out.println("👥 Hospital total staff: " + mainHospital.getStaffCount());
        
        System.out.println("\n🎉 Complete workflow executed using all design patterns!");
    }
    
    private Patient createSamplePatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDob(LocalDate.of(1970, 5, 15)); // 53 years old
        patient.setGender("Male");
        patient.setContactNumber("555-1234");
        patient.setAddress("123 Main Street");
        return patient;
    }
    
    private Staff createSampleDoctor() {
        Role doctorRole = new Role();
        doctorRole.setId(1L);
        doctorRole.setName("Doctor");
        
        Staff doctor = new Staff();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        doctor.setEmail("dr.smith@globemed.lk");
        doctor.setRole(doctorRole);
        return doctor;
    }
    
    // Getters for external access
    public Hospital getMainHospital() {
        return mainHospital;
    }
    
    public AppointmentPrototypeRegistry getPrototypeRegistry() {
        return prototypeRegistry;
    }
    
    public ApprovalHandler getApprovalChain() {
        return approvalChain;
    }
}