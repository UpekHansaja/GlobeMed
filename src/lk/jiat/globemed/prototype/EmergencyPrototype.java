package lk.jiat.globemed.prototype;

/**
 * Concrete Prototype for emergency appointments
 */
public class EmergencyPrototype extends AppointmentPrototype {

    public EmergencyPrototype() {
        super("Emergency", 60, "URGENT");
        this.notes = "Urgent medical attention required - immediate assessment and treatment";
        this.estimatedCost = 250.00;
        this.requiredEquipment = "Emergency cart, Defibrillator, Oxygen, IV supplies, Monitoring equipment";
        this.preparationInstructions = "Immediate attention - no preparation time required";
    }

    @Override
    public AppointmentPrototype clone() {
        EmergencyPrototype cloned = (EmergencyPrototype) super.clone();
        // Deep copy if needed for complex objects (Strings are immutable in Java, so this is optional)
        cloned.notes = this.notes;
        cloned.requiredEquipment = this.requiredEquipment;
        cloned.preparationInstructions = this.preparationInstructions;
        
        System.out.println("🚨 Cloned EmergencyPrototype");
        return cloned;
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Emergency Appointment ===");
        System.out.println("Type: " + type);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Priority: " + priority);
        System.out.println("Estimated Cost: $" + estimatedCost);
        System.out.println("Notes: " + notes);
        System.out.println("Required Equipment: " + requiredEquipment);
        System.out.println("Preparation: " + preparationInstructions);
        System.out.println("==============================");
    }
}
