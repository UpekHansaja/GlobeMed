# GlobeMed Healthcare Management System

A comprehensive healthcare management system built with Java Swing and Hibernate, implementing advanced Object-Oriented Design Patterns (OODP2). This system provides complete functionality for managing patients, staff, appointments, billing, and hospital operations.

## 🏥 System Features

### Core Functionality
- **Patient Management**: Complete patient registration, medical history, and profile management
- **Staff Management**: Role-based access control for Doctors, Nurses, Pharmacists, Accountants, and Administrators
- **Appointment Scheduling**: Advanced appointment booking with time slot management and conflict resolution
- **Billing System**: Comprehensive billing and payment tracking with automated calculations
- **Audit Logging**: Complete audit trail for all system operations and user activities
- **Notification System**: Multi-channel notifications (Email/SMS) for appointments and billing
- **Reporting**: Advanced reporting system with CSV export capabilities
- **Security**: Role-based permissions and secure authentication

### Advanced Design Patterns Implemented
- **Composite Pattern**: Hospital hierarchy management (Hospitals → Departments → Units)
- **Bridge Pattern**: Notification system with multiple delivery channels
- **Builder Pattern**: Complex appointment creation with validation
- **Chain of Responsibility**: Multi-level approval workflows
- **Flyweight Pattern**: Optimized time slot management
- **Interpreter Pattern**: Advanced query language for medical records
- **Mediator Pattern**: UI component coordination and communication
- **Prototype Pattern**: Appointment templates and cloning
- **Decorator Pattern**: Enhanced DAO operations with audit logging
- **Visitor Pattern**: Flexible report generation system
- **Memento Pattern**: Undo functionality for critical operations

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- NetBeans IDE (recommended) or any Java IDE
- Maven (for dependency management)

### Database Setup
1. Create a MySQL database named `globemed`
2. Update database credentials in `src/hibernate.cfg.xml`
3. The system will automatically create tables on first run

### Running the Application
1. Clone the repository
2. Open the project in NetBeans IDE
3. Build and run the `MainApp.java` file
4. Use default admin credentials: `admin@globemed.lk` / `admin123`

## 🏗️ Architecture Overview

The system follows a layered architecture with clear separation of concerns:

```
src/lk/jiat/globemed/
├── app/                    # Application entry point
├── model/                  # JPA Entity classes
├── dao/                    # Data Access Layer
├── service/                # Business Logic Layer
├── ui/                     # Presentation Layer (Swing Forms)
├── builder/                # Builder Pattern Implementation
├── interpreter/            # Query Language Implementation
├── mediator/              # UI Coordination
├── prototype/             # Template Management
├── service/
│   ├── approval/          # Approval Workflow System
│   ├── command/           # Command Pattern Implementation
│   └── notification/      # Multi-channel Notification System
├── antipattern/           # Anti-pattern Examples (Educational)
└── util/                  # Utility Classes
```

## 👥 User Roles and Permissions

### Administrator
- Complete system access
- User management (add/edit/delete staff)
- System configuration
- Advanced reporting and analytics
- Audit log review

### Doctor
- Patient management
- Appointment scheduling and management
- Medical record access
- Prescription management
- Department-specific reporting

### Nurse
- Patient care coordination
- Appointment assistance
- Basic patient information updates
- Medication tracking

### Pharmacist
- Prescription management
- Inventory tracking
- Drug interaction checking
- Pharmacy reporting

### Accountant
- Billing management
- Payment processing
- Financial reporting
- Insurance claim processing

## 🔧 Key Technical Features

### Design Pattern Benefits
- **Flexibility**: Easy to extend with new features
- **Maintainability**: Clean, organized code structure
- **Scalability**: Efficient resource management
- **Testability**: Modular components for easy testing
- **Reusability**: Components can be reused across modules

### Database Integration
- Hibernate ORM with JPA annotations
- Automatic schema generation
- Connection pooling
- Transaction management
- Audit logging

### User Interface
- Modern Swing UI with FlatLaf theme
- Responsive design
- Role-based menu systems
- Real-time data updates
- Form validation

## 📊 System Capabilities

### Appointment Management
- Conflict-free scheduling
- Recurring appointments
- Appointment templates
- Automated reminders
- Status tracking

### Billing System
- Automated billing calculations
- Multiple payment methods
- Insurance integration
- Payment tracking
- Financial reporting

### Reporting System
- Patient reports
- Staff performance reports
- Financial reports
- Appointment analytics
- Custom report generation

### Security Features
- Role-based access control
- Secure password handling
- Session management
- Audit logging
- Data encryption

## 🎯 Educational Value

This system demonstrates:
- Advanced Object-Oriented Design Principles
- Real-world application of Design Patterns
- Enterprise Java development practices
- Database design and ORM usage
- UI/UX design principles
- Software architecture best practices

## 📈 Future Enhancements

Potential improvements:
- Web-based interface
- Mobile application
- Integration with medical devices
- Telemedicine capabilities
- Advanced analytics and AI
- Cloud deployment options

---

**GlobeMed Healthcare Management System** - A complete, production-ready healthcare management solution demonstrating advanced software engineering principles and design patterns.