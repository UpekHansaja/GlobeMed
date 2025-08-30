package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DEPARTMENT")
public class Department extends MedicalUnit {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "department_staff",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    private List<Staff> staff;

    @Column(name = "budget")
    private double budget;

    @Column(name = "department_code", unique = true)
    private String departmentCode;

    public Department(String name, String description, String departmentCode, double budget) {
        super(name, description);
        this.departmentCode = departmentCode;
        this.budget = budget;
        this.staff = new ArrayList<>();
    }

    @Override
    public void displayInfo() {
        System.out.println("Department: " + name);
        System.out.println("Code: " + departmentCode);
        System.out.println("Description: " + description);
        System.out.println("Budget: $" + budget);
        System.out.println("Staff Count: " + staff.size());
        System.out.println("Staff Members:");
        for (Staff member : staff) {
            System.out.println("  - " + member.getName() + " ("
                    + (member.getRole() != null ? member.getRole().getName() : "No Role") + ")");
        }
    }

    @Override
    public double getTotalBudget() {
        return budget;
    }

    @Override
    public int getStaffCount() {
        return staff.size();
    }

    @Override
    public List<Staff> getAllStaff() {
        return new ArrayList<>(staff);
    }

    public void addStaff(Staff staffMember) {
        if (!staff.contains(staffMember)) {
            staff.add(staffMember);
        }
    }

    public void removeStaff(Staff staffMember) {
        staff.remove(staffMember);
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
