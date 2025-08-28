# GlobeMed - Advanced Design Patterns Implementation

## Overview
This project demonstrates the implementation of advanced Object-Oriented Design Patterns (OODP2) in a hospital management system called GlobeMed. The system showcases 8 advanced design patterns, compound patterns, and anti-patterns to avoid.

## Implemented Design Patterns

### 1. Composite Pattern 🏥
**Location**: `src/lk/jiat/globemed/model/`
- **Files**: `MedicalUnit.java`, `Hospital.java`, `Department.java`
- **Purpose**: Represents hospital hierarchy where hospitals contain departments
- **Key Features**:
  - Tree structure for medical units
  - Uniform interface for individual departments and hospital composites
  - Recursive operations (budget calculation, staff counting)

**Example Usage**:
```java
Hospital hospital = new Hospital("GlobeMed Central", "Main facility", "GMC001", "123 Medical Drive");
Department cardiology = new Department("Cardiology", "Heart care", "CARD", 50000.0);
hospital.add(cardiology);
hospital.displayInfo(); // Shows entire hierarchy
```

### 2. Bridge Pattern 🌉
**Location**: `src/lk/jiat/globemed/service/notification/`
- **Files**: `NotificationSender.java`, `EmailSender.java`, `SMSSender.java`, `Notification.java`, `AppointmentNotification.java`, `BillingNotification.java`
- **Purpose**: Separates notification abstraction from implementation
- **Key Features**:
  - Different notification types (Appointment, Billing)
  - Multiple sending mechanisms (Email, SMS)
  - Easy to add new notification types or senders

**Example Usage**:
```java
EmailSender emailSender = new EmailSender("smtp.hospital.com", 587, "system@hospital.com", "password");
AppointmentNotification notification = new AppointmentNotification(emailSender, appointment, "SCHEDULED");
notification.notify("patient@email.com");
```

### 3. Builder Pattern 🔨
**Location**: `src/lk/jiat/globemed/builder/`
- **Files**: `AppointmentBuilder.java`, `AppointmentDirector.java`
- **Purpose**: Constructs complex appointment objects with validation
- **Key Features**:
  - Fluent interface for appointment creation
  - Built-in validation (past dates, doctor roles, working hours)
  - Director class for common appointment types

**Example Usage**:
```java
AppointmentBuilder builder = new AppointmentBuilder();
Appointment appointment = builder
    .withPatient(patient)
    .withDoctor(doctor)
    .withDateTime(LocalDateTime.now().plusDays(1))
    .withStatus("Consultation")
    .build();
```

### 4. Chain of Responsibility Pattern ⛓️
**Location**: `src/lk/jiat/globemed/service/approval/`
- **Files**: `ApprovalHandler.java`, `NurseApprovalHandler.java`, `DoctorApprovalHandler.java`, `AdminApprovalHandler.java`, `ApprovalRequest.java`, `ApprovalChainBuilder.java`
- **Purpose**: Handles approval workflow based on cost and complexity
- **Key Features**:
  - Three-tier approval system (Nurse → Doctor → Admin)
  - Different approval limits for each role
  - Automatic forwarding to next handler if current can't handle

**Example Usage**:
```java
ApprovalHandler chain = ApprovalChainBuilder.createStandardChain();
ApprovalRequest request = new ApprovalRequest("SURGERY", 2500.0, "doctor@hospital.com", "Minor surgery", appointment);
chain.handleRequest(request);
```

### 5. Flyweight Pattern 🪶
**Location**: `src/lk/jiat/globemed/service/`, `src/lk/jiat/globemed/model/`
- **Files**: `TimeSlotFactory.java`, `TimeSlot.java`
- **Purpose**: Optimizes memory usage for appointment time slots
- **Key Features**:
  - Intrinsic state: time, duration, type (shared)
  - Extrinsic state: doctor, patient, date (passed as parameters)
  - Significant memory savings for recurring time slots

**Example Usage**:
```java
TimeSlot slot1 = TimeSlotFactory.getConsultationSlot("09:00", 30);
TimeSlot slot2 = TimeSlotFactory.getConsultationSlot("09:00", 30); // Same instance
slot1.scheduleAppointment("Dr. Smith", "John Doe", LocalDate.now(), "Scheduled");
```

### 6. Interpreter Pattern 🔍
**Location**: `src/lk/jiat/globemed/interpreter/`
- **Files**: `Expression.java`, `QueryContext.java`, `PatientAgeExpression.java`, `PatientGenderExpression.java`, `AppointmentStatusExpression.java`, `AndExpression.java`, `OrExpression.java`, `NotExpression.java`, `QueryBuilder.java`
- **Purpose**: Provides a query language for medical records
- **Key Features**:
  - Terminal expressions for patient age, gender, appointment status
  - Non-terminal expressions for logical operations (AND, OR, NOT)
  - QueryBuilder for easier query construction

**Example Usage**:
```java
Expression query = new AndExpression(
    new PatientAgeExpression(">=", 65),
    new PatientGenderExpression("Male")
);
QueryContext context = new QueryContext();
context.setVariable("patient", patient);
boolean result = query.interpret(context);
```

### 7. Mediator Pattern 🔄
**Location**: `src/lk/jiat/globemed/mediator/`
- **Files**: `DashboardMediator.java`, `AdminDashboardMediator.java`
- **Purpose**: Coordinates interactions between UI components
- **Key Features**:
  - Centralized communication between UI components
  - Loose coupling between components
  - Event-driven architecture

**Example Usage**:
```java
AdminDashboardMediator mediator = new AdminDashboardMediator(
    addBtn, editBtn, deleteBtn, refreshBtn, table, searchField, roleFilter, statusLabel);
mediator.notify(table, "SELECTION_CHANGED", null);
```

### 8. Prototype Pattern 📋
**Location**: `src/lk/jiat/globemed/prototype/`
- **Files**: `AppointmentPrototype.java`, `RoutineCheckupPrototype.java`, `EmergencyPrototype.java`, `SurgeryPrototype.java`, `AppointmentPrototypeRegistry.java`
- **Purpose**: Creates appointment templates through cloning
- **Key Features**:
  - Pre-configured appointment templates
  - Registry for managing prototypes
  - Deep cloning for complex objects

**Example Usage**:
```java
AppointmentPrototypeRegistry registry = AppointmentPrototypeRegistry.getInstance();
AppointmentPrototype routine = registry.getPrototype("routine");
routine.displayInfo();
```

## Already Implemented Patterns (from OODP1)

### 9. Decorator Pattern ✨
**Location**: `src/lk/jiat/globemed/dao/`
- **Files**: `StaffDaoAuditDecorator.java`
- **Purpose**: Adds auditing functionality to staff operations

### 10. Visitor Pattern 👁️
**Location**: `src/lk/jiat/globemed/report/`
- **Files**: `ReportVisitor.java`, `CSVReportVisitor.java`
- **Purpose**: Generates different types of reports

### 11. Memento Pattern 💾
**Location**: `src/lk/jiat/globemed/service/`
- **Files**: `StaffService.java` (contains StaffMemento inner class)
- **Purpose**: Provides undo functionality for staff operations

## Compound Patterns

### MVC + Observer + Strategy
The system combines multiple patterns:
- **Model-View-Controller**: UI forms with business logic separation
- **Observer**: Event-driven UI updates
- **Strategy**: Different approval strategies in Chain of Responsibility

## Anti-Patterns (What to Avoid) ❌

### 1. God Object Anti-Pattern
**Location**: `src/lk/jiat/globemed/antipattern/GodObjectExample.java`
- **Problem**: Single class trying to handle everything
- **Issues**: Violates SRP, hard to maintain, high coupling
- **Solution**: Break into separate service classes

### 2. Spaghetti Code Anti-Pattern
**Location**: `src/lk/jiat/globemed/antipattern/SpaghettiCodeExample.java`
- **Problem**: Deeply nested if-else statements, complex control flow
- **Issues**: Hard to understand, test, and modify
- **Solution**: Use early returns, extract methods, apply design patterns

## Architecture Overview

```
src/lk/jiat/globemed/
├── app/                    # Application entry point
├── model/                  # Entity classes (JPA annotated)
├── dao/                    # Data Access Objects
├── service/                # Business logic services
├── ui/                     # Swing UI forms
├── builder/                # Builder pattern implementation
├── interpreter/            # Interpreter pattern implementation
├── mediator/              # Mediator pattern implementation
├── prototype/             # Prototype pattern implementation
├── service/
│   ├── approval/          # Chain of Responsibility
│   └── notification/      # Bridge pattern
├── antipattern/           # Anti-pattern examples
└── util/                  # Utility classes
```

## Key Features

### 1. Pattern Demo UI
- **File**: `PatternDemoForm.java`
- Interactive demonstration of all patterns
- Tabbed interface for each pattern
- Real-time output showing pattern behavior

### 2. Comprehensive Integration
- **File**: `PatternDemoService.java`
- Shows all patterns working together
- Real-world scenario implementation
- Complete workflow demonstration

### 3. Database Integration
- Hibernate ORM with MySQL
- JPA annotations on entities
- Automatic schema generation

### 4. NetBeans Compatibility
- `.form` files for visual design
- Compatible with NetBeans IDE
- Proper separation of generated and custom code

## Running the Application

1. **Prerequisites**:
   - Java 17+
   - MySQL database
   - NetBeans IDE (recommended)

2. **Database Setup**:
   - Create MySQL database named `globemed`
   - Update credentials in `hibernate.cfg.xml`

3. **Running**:
   - Run `MainApp.java`
   - Choose "Design Patterns Demo" to see pattern demonstrations
   - Choose "Login to System" for normal application

## Pattern Benefits Demonstrated

1. **Flexibility**: Easy to add new notification types, approval handlers, or appointment templates
2. **Maintainability**: Clear separation of concerns, single responsibility
3. **Extensibility**: New patterns can be added without modifying existing code
4. **Reusability**: Components can be reused across different contexts
5. **Testability**: Each pattern component can be tested independently

## Learning Outcomes

This implementation demonstrates:
- Advanced OOP design principles
- Real-world application of design patterns
- Integration of multiple patterns
- Best practices in software architecture
- What to avoid (anti-patterns)

## Future Enhancements

Potential additions:
- State pattern for appointment lifecycle
- Template Method for report generation
- Factory pattern for user creation
- Observer pattern for real-time notifications
- Command pattern enhancements with macro commands

---

**Note**: This project is designed for educational purposes to demonstrate advanced design patterns in a practical, real-world context. Each pattern is implemented with proper documentation and examples to facilitate learning and understanding.