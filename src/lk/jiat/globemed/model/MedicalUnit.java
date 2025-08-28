package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Component interface for Composite Pattern
 * Represents a medical unit in the hospital hierarchy
 */
@Entity
@Table(name = "medical_units")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "unit_type", discriminatorType = DiscriminatorType.STRING)
public abstract class MedicalUnit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    protected String name;
    protected String description;
    
    public MedicalUnit(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public abstract void displayInfo();
    public abstract double getTotalBudget();
    public abstract int getStaffCount();
    public abstract List<Staff> getAllStaff();
    
    // Optional operations for composite
    public void add(MedicalUnit unit) {
        throw new UnsupportedOperationException("Add operation not supported");
    }
    
    public void remove(MedicalUnit unit) {
        throw new UnsupportedOperationException("Remove operation not supported");
    }
    
    public List<MedicalUnit> getChildren() {
        throw new UnsupportedOperationException("GetChildren operation not supported");
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}