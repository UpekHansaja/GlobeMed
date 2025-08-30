package lk.jiat.globemed.prototype;

public abstract class AppointmentPrototype implements Cloneable {

    protected String type;
    protected int duration; // minutes
    protected String notes;
    protected String priority;
    protected double estimatedCost;
    protected String requiredEquipment;
    protected String preparationInstructions;

    public AppointmentPrototype(String type, int duration, String priority) {
        this.type = type;
        this.duration = duration;
        this.priority = priority;
        this.estimatedCost = 0.0;
        this.requiredEquipment = "";
        this.preparationInstructions = "";
        this.notes = "";
    }

    @Override
    public AppointmentPrototype clone() {
        try {
            return (AppointmentPrototype) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone AppointmentPrototype", e);
        }
    }

    public abstract void displayInfo();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(String requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public String getPreparationInstructions() {
        return preparationInstructions;
    }

    public void setPreparationInstructions(String preparationInstructions) {
        this.preparationInstructions = preparationInstructions;
    }

    @Override
    public String toString() {
        return String.format("AppointmentPrototype{type='%s', duration=%d, priority='%s', cost=%.2f}",
                type, duration, priority, estimatedCost);
    }
}
