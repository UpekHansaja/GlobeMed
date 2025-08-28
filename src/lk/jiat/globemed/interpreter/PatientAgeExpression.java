package lk.jiat.globemed.interpreter;

import lk.jiat.globemed.model.Patient;
import java.time.LocalDate;
import java.time.Period;

/**
 * Terminal Expression for Interpreter Pattern
 * Evaluates patient age conditions
 */
public class PatientAgeExpression implements Expression {
    private int age;
    private String operator; // ">", "<", "=", ">=", "<="
    
    public PatientAgeExpression(String operator, int age) {
        this.operator = operator;
        this.age = age;
    }
    
    @Override
    public boolean interpret(QueryContext context) {
        Patient patient = (Patient) context.getVariable("patient");
        if (patient == null || patient.getDob() == null) {
            return false;
        }
        
        int patientAge = calculateAge(patient.getDob());
        
        switch (operator) {
            case ">":
                return patientAge > age;
            case "<":
                return patientAge < age;
            case "=":
            case "==":
                return patientAge == age;
            case ">=":
                return patientAge >= age;
            case "<=":
                return patientAge <= age;
            default:
                return false;
        }
    }
    
    private int calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }
    
    @Override
    public String toString() {
        return String.format("age %s %d", operator, age);
    }
}