package lk.jiat.globemed.prototype;

public class SurgeryPrototype extends AppointmentPrototype {

    public SurgeryPrototype() {
        super("Surgery", 180, "HIGH");
        this.notes = "Surgical procedure requiring operating room and specialized staff";
        this.estimatedCost = 1500.00;
        this.requiredEquipment = "Operating room, Surgical instruments, Anesthesia equipment, Monitoring systems";
        this.preparationInstructions = "NPO (nothing by mouth) 8 hours before surgery. Pre-operative assessment required.";
    }

    @Override
    public AppointmentPrototype clone() {
        SurgeryPrototype cloned = (SurgeryPrototype) super.clone();

        cloned.notes = this.notes;
        cloned.requiredEquipment = this.requiredEquipment;
        cloned.preparationInstructions = this.preparationInstructions;

        System.out.println("Cloned SurgeryPrototype");
        return cloned;
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Surgery Appointment ===");
        System.out.println("Type: " + type);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Priority: " + priority);
        System.out.println("Estimated Cost: $" + estimatedCost);
        System.out.println("Notes: " + notes);
        System.out.println("Required Equipment: " + requiredEquipment);
        System.out.println("Preparation: " + preparationInstructions);
        System.out.println("============================");
    }
}
