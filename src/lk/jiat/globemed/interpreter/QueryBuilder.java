package lk.jiat.globemed.interpreter;

/**
 * Builder class to help construct complex query expressions
 * Provides a fluent interface for building queries
 */
public class QueryBuilder {
    private Expression currentExpression;
    
    public QueryBuilder() {
        this.currentExpression = null;
    }
    
    public QueryBuilder patientAge(String operator, int age) {
        Expression ageExpr = new PatientAgeExpression(operator, age);
        if (currentExpression == null) {
            currentExpression = ageExpr;
        } else {
            currentExpression = new AndExpression(currentExpression, ageExpr);
        }
        return this;
    }
    
    public QueryBuilder patientGender(String gender) {
        Expression genderExpr = new PatientGenderExpression(gender);
        if (currentExpression == null) {
            currentExpression = genderExpr;
        } else {
            currentExpression = new AndExpression(currentExpression, genderExpr);
        }
        return this;
    }
    
    public QueryBuilder appointmentStatus(String status) {
        Expression statusExpr = new AppointmentStatusExpression(status);
        if (currentExpression == null) {
            currentExpression = statusExpr;
        } else {
            currentExpression = new AndExpression(currentExpression, statusExpr);
        }
        return this;
    }
    
    public QueryBuilder and(Expression expression) {
        if (currentExpression == null) {
            currentExpression = expression;
        } else {
            currentExpression = new AndExpression(currentExpression, expression);
        }
        return this;
    }
    
    public QueryBuilder or(Expression expression) {
        if (currentExpression == null) {
            currentExpression = expression;
        } else {
            currentExpression = new OrExpression(currentExpression, expression);
        }
        return this;
    }
    
    public QueryBuilder not() {
        if (currentExpression != null) {
            currentExpression = new NotExpression(currentExpression);
        }
        return this;
    }
    
    public Expression build() {
        return currentExpression;
    }
    
    public QueryBuilder reset() {
        currentExpression = null;
        return this;
    }
    
    // Static factory methods for common queries
    public static Expression seniorPatients() {
        return new PatientAgeExpression(">=", 65);
    }
    
    public static Expression pediatricPatients() {
        return new PatientAgeExpression("<", 18);
    }
    
    public static Expression adultMalePatients() {
        return new AndExpression(
            new PatientAgeExpression(">=", 18),
            new PatientGenderExpression("Male")
        );
    }
    
    public static Expression scheduledAppointments() {
        return new AppointmentStatusExpression("Scheduled");
    }
    
    public static Expression cancelledAppointments() {
        return new AppointmentStatusExpression("Cancelled");
    }
    
    public static Expression emergencyAppointments() {
        return new OrExpression(
            new AppointmentStatusExpression("EMERGENCY"),
            new AppointmentStatusExpression("URGENT")
        );
    }
}