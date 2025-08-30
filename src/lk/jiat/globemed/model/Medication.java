package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false, length = 50)
    private String category; // Antibiotics, Paiin-Relief, Vitamins, etc.

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer minimumStock;

    @Column(length = 50)
    private String dosageForm; // Tablet, Capsule, Syrup, Injection

    @Column(length = 50)
    private String strength; // 500mg, 10ml, etc.

    private LocalDate expiryDate;

    @Column(length = 50)
    private String batchNumber;

    @Column(length = 20)
    private String status; // Available, Out of Stock, Expired, Discontinued

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL)
    private List<PrescriptionItem> prescriptionItems;

    public Medication() {
    }

    public Medication(String name, String category, String manufacturer, Double unitPrice,
            Integer stockQuantity, Integer minimumStock, String dosageForm, String strength) {
        this.name = name;
        this.category = category;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.status = "Available";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PrescriptionItem> getPrescriptionItems() {
        return prescriptionItems;
    }

    public void setPrescriptionItems(List<PrescriptionItem> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }

    public boolean isLowStock() {
        return stockQuantity <= minimumStock;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now().plusMonths(3));
    }

    public void dispense(int quantity) {
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
            if (stockQuantity == 0) {
                status = "Out of Stock";
            }
        } else {
            throw new IllegalArgumentException("Insufficient stock. Available: " + stockQuantity + ", Requested: " + quantity);
        }
    }

    public void restock(int quantity) {
        stockQuantity += quantity;
        if ("Out of Stock".equals(status) && stockQuantity > 0) {
            status = "Available";
        }
    }

    @Override
    public String toString() {
        return name + " (" + strength + ") - " + dosageForm;
    }
}
