package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("HOSPITAL")
public class Hospital extends MedicalUnit {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_hospital_id")
    private List<MedicalUnit> units;

    @Column(name = "hospital_code", unique = true)
    private String hospitalCode;

    @Column(name = "address")
    private String address;

    public Hospital(String name, String description, String hospitalCode, String address) {
        super(name, description);
        this.hospitalCode = hospitalCode;
        this.address = address;
        this.units = new ArrayList<>();
    }

    @Override
    public void add(MedicalUnit unit) {
        if (!units.contains(unit)) {
            units.add(unit);
        }
    }

    @Override
    public void remove(MedicalUnit unit) {
        units.remove(unit);
    }

    @Override
    public List<MedicalUnit> getChildren() {
        return new ArrayList<>(units);
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Hospital: " + name + " ===");
        System.out.println("Code: " + hospitalCode);
        System.out.println("Address: " + address);
        System.out.println("Description: " + description);
        System.out.println("Total Budget: $" + getTotalBudget());
        System.out.println("Total Staff: " + getStaffCount());
        System.out.println("\nMedical Units:");

        for (MedicalUnit unit : units) {
            System.out.println("\n" + "-".repeat(40));
            unit.displayInfo();
        }
        System.out.println("=".repeat(50));
    }

    @Override
    public double getTotalBudget() {
        return units.stream()
                .mapToDouble(MedicalUnit::getTotalBudget)
                .sum();
    }

    @Override
    public int getStaffCount() {
        return units.stream()
                .mapToInt(MedicalUnit::getStaffCount)
                .sum();
    }

    @Override
    public List<Staff> getAllStaff() {
        List<Staff> allStaff = new ArrayList<>();
        for (MedicalUnit unit : units) {
            allStaff.addAll(unit.getAllStaff());
        }
        return allStaff;
    }

    public MedicalUnit findUnitByName(String unitName) {

        for (MedicalUnit unit : units) {

            if (unit.getName().equalsIgnoreCase(unitName)) {
                return unit;
            }

            if (unit instanceof Hospital) {
                MedicalUnit found = ((Hospital) unit).findUnitByName(unitName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public List<MedicalUnit> getUnits() {
        return units;
    }

    public void setUnits(List<MedicalUnit> units) {
        this.units = units;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
