package lk.jiat.globemed.interpreter;

import lk.jiat.globemed.model.Patient;

public class PatientGenderExpression implements Expression {

    private String gender;

    public PatientGenderExpression(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean interpret(QueryContext context) {
        Patient patient = (Patient) context.getVariable("patient");
        if (patient == null || patient.getGender() == null) {
            return false;
        }

        return patient.getGender().equalsIgnoreCase(gender);
    }

    @Override
    public String toString() {
        return String.format("gender = '%s'", gender);
    }
}
