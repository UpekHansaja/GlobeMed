package lk.jiat.globemed.prototype;

/**
 * Concrete Prototype for routine checkup appointments
 */
public class RoutineCheckupPrototype extends AppointmentPrototype {
    
    public RoutineCheckupPrototype() {
        super("Routine Checkup", 30, "MEDIUM");
        this.notes = "Standard health checkup including vital signs, basic examination";
        this.estimatedCost = 75.00;
        this.requiredEquipment = "Stethoscope, Blood pressure monitor, Thermometer, Scale";
        this.preparationInstructions = "Please arrive 15 minutes early. Bring insurance card and ID.";
    }
    
    @Override
    public AppointmentPrototype clone() {
        RoutineCheckupPrototype cloned = (RoutineCheckupPrototype) super.clone();
        // Deep copy if needed for complex objects (Strings are immutable in Java, so this is optional)
        cloned.notes = this.notes;
        cloned.requiredEquipment = this.requiredEquipment;
        cloned.preparationInstructions = this.preparationInstructions;
        
        System.out.println("🔄 Cloned RoutineCheckupPrototype");
        return cloned;
    }
    
    @Override
    public void displayInfo() {
        System.out.println("=== Routine Checkup Appointment ===");
        System.out.println("Type: " + type);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Priority: " + priority);
        System.out.println("Estimated Cost: $" + estimatedCost);
        System.out.println("Notes: " + notes);
        System.out.println("Required Equipment: " + requiredEquipment);
        System.out.println("Preparation: " + preparationInstructions);
        System.out.println("=====================================");
    }
}