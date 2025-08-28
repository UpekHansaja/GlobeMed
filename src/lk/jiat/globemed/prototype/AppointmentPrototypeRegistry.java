package lk.jiat.globemed.prototype;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for managing appointment prototypes
 * Provides centralized access to prototype instances
 */
public class AppointmentPrototypeRegistry {
    private Map<String, AppointmentPrototype> prototypes;
    private static AppointmentPrototypeRegistry instance;
    
    private AppointmentPrototypeRegistry() {
        this.prototypes = new HashMap<>();
        initializePrototypes();
    }
    
    public static AppointmentPrototypeRegistry getInstance() {
        if (instance == null) {
            instance = new AppointmentPrototypeRegistry();
        }
        return instance;
    }
    
    private void initializePrototypes() {
        System.out.println("🏗️ Initializing appointment prototypes...");
        
        // Register standard prototypes
        prototypes.put("routine", new RoutineCheckupPrototype());
        prototypes.put("emergency", new EmergencyPrototype());
        prototypes.put("surgery", new SurgeryPrototype());
        
        // Create additional specialized prototypes
        createSpecializedPrototypes();
        
        System.out.println("✅ Initialized " + prototypes.size() + " appointment prototypes");
    }
    
    private void createSpecializedPrototypes() {
        // Consultation prototype
        AppointmentPrototype consultation = new RoutineCheckupPrototype();
        consultation.setType("Consultation");
        consultation.setDuration(45);
        consultation.setEstimatedCost(100.00);
        consultation.setNotes("Detailed consultation with specialist");
        prototypes.put("consultation", consultation);
        
        // Follow-up prototype
        AppointmentPrototype followUp = new RoutineCheckupPrototype();
        followUp.setType("Follow-up");
        followUp.setDuration(20);
        followUp.setEstimatedCost(50.00);
        followUp.setNotes("Follow-up visit to review treatment progress");
        followUp.setPreparationInstructions("Bring previous test results and medication list");
        prototypes.put("followup", followUp);
        
        // Physical therapy prototype
        AppointmentPrototype physicalTherapy = new RoutineCheckupPrototype();
        physicalTherapy.setType("Physical Therapy");
        physicalTherapy.setDuration(60);
        physicalTherapy.setEstimatedCost(80.00);
        physicalTherapy.setNotes("Physical therapy session for rehabilitation");
        physicalTherapy.setRequiredEquipment("Exercise equipment, Therapy table, Resistance bands");
        physicalTherapy.setPreparationInstructions("Wear comfortable exercise clothing");
        prototypes.put("physicaltherapy", physicalTherapy);
        
        // Diagnostic test prototype
        AppointmentPrototype diagnostic = new RoutineCheckupPrototype();
        diagnostic.setType("Diagnostic Test");
        diagnostic.setDuration(90);
        diagnostic.setEstimatedCost(200.00);
        diagnostic.setNotes("Diagnostic testing and imaging");
        diagnostic.setRequiredEquipment("Imaging equipment, Lab supplies");
        diagnostic.setPreparationInstructions("Fasting may be required - check specific test requirements");
        prototypes.put("diagnostic", diagnostic);
    }
    
    public AppointmentPrototype getPrototype(String type) {
        AppointmentPrototype prototype = prototypes.get(type.toLowerCase());
        if (prototype == null) {
            throw new IllegalArgumentException("Unknown appointment type: " + type);
        }
        
        AppointmentPrototype cloned = prototype.clone();
        System.out.println("📋 Retrieved prototype: " + type);
        return cloned;
    }
    
    public void registerPrototype(String type, AppointmentPrototype prototype) {
        prototypes.put(type.toLowerCase(), prototype);
        System.out.println("📝 Registered new prototype: " + type);
    }
    
    public void unregisterPrototype(String type) {
        AppointmentPrototype removed = prototypes.remove(type.toLowerCase());
        if (removed != null) {
            System.out.println("🗑️ Unregistered prototype: " + type);
        }
    }
    
    public Set<String> getAvailableTypes() {
        return prototypes.keySet();
    }
    
    public int getPrototypeCount() {
        return prototypes.size();
    }
    
    public void displayAllPrototypes() {
        System.out.println("\n=== Available Appointment Prototypes ===");
        for (Map.Entry<String, AppointmentPrototype> entry : prototypes.entrySet()) {
            System.out.println("\nType: " + entry.getKey().toUpperCase());
            entry.getValue().displayInfo();
        }
        System.out.println("========================================\n");
    }
    
    /**
     * Create a customized prototype based on an existing one
     */
    public AppointmentPrototype createCustomPrototype(String baseType, String newType, 
                                                     int duration, double cost, String notes) {
        AppointmentPrototype base = getPrototype(baseType);
        base.setType(newType);
        base.setDuration(duration);
        base.setEstimatedCost(cost);
        base.setNotes(notes);
        
        System.out.println("🎨 Created custom prototype: " + newType + " (based on " + baseType + ")");
        return base;
    }
    
    /**
     * Bulk create appointments from prototype
     */
    public AppointmentPrototype[] createMultiple(String type, int count) {
        AppointmentPrototype[] appointments = new AppointmentPrototype[count];
        
        for (int i = 0; i < count; i++) {
            appointments[i] = getPrototype(type);
        }
        
        System.out.println("📚 Created " + count + " appointments of type: " + type);
        return appointments;
    }
    
    /**
     * Reset registry to default prototypes
     */
    public void reset() {
        prototypes.clear();
        initializePrototypes();
        System.out.println("🔄 Registry reset to default prototypes");
    }
}