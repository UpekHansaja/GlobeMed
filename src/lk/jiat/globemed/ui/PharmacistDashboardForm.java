package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lk.jiat.globemed.dao.MedicationDao;
import lk.jiat.globemed.dao.PrescriptionDao;
import lk.jiat.globemed.model.Medication;
import lk.jiat.globemed.model.Prescription;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.PharmacistService;

public class PharmacistDashboardForm extends JFrame {

    private Staff currentUser;
    private final PharmacistService pharmacistService = new PharmacistService();
    private final MedicationDao medicationDao = new MedicationDao();
    private final PrescriptionDao prescriptionDao = new PrescriptionDao();

    // UI Components
    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnProfile;
    private JTabbedPane tabMain;

    // Inventory Tab Components
    private JTable tblMedications;
    private DefaultTableModel medicationTableModel;
    private JButton btnAddMedication;
    private JButton btnEditMedication;
    private JButton btnDispense;
    private JButton btnRestock;
    private JButton btnRefreshInventory;
    private JTextField txtSearchMedication;
    private JComboBox<String> cmbCategoryFilter;
    private JLabel lblInventoryStats;

    // Prescriptions Tab Components
    private JTable tblPrescriptions;
    private DefaultTableModel prescriptionTableModel;
    private JButton btnFillPrescription;
    private JButton btnViewPrescription;
    private JButton btnRefreshPrescriptions;
    private JComboBox<String> cmbStatusFilter;

    // Reports Tab Components
    private JTextArea txtReportArea;
    private JButton btnGenerateInventoryReport;
    private JButton btnLowStockReport;
    private JButton btnExpiryReport;
    private JButton btnExportReport;

    public PharmacistDashboardForm() {
        initComponents();
        postInit(null);
    }

    public PharmacistDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        lblTitle = new JLabel("💊 Pharmacist Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnProfile = new JButton("👤 Profile");
        btnLogout = new JButton("🚪 Logout");
        headerRight.add(btnProfile);
        headerRight.add(btnLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(headerPanel, BorderLayout.NORTH);

        // Main Tabbed Pane
        tabMain = new JTabbedPane();

        // Create tabs
        tabMain.addTab("📦 Inventory", createInventoryTab());
        tabMain.addTab("📋 Prescriptions", createPrescriptionsTab());
        tabMain.addTab("📊 Reports", createReportsTab());

        add(tabMain, BorderLayout.CENTER);
    }

    private JPanel createInventoryTab() {
        JPanel tabInventory = new JPanel(new BorderLayout(5, 5));
        tabInventory.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with search and filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("🔍 Search:"));
        txtSearchMedication = new JTextField(15);
        txtSearchMedication.setToolTipText("Search medications by name");
        searchPanel.add(txtSearchMedication);

        searchPanel.add(new JLabel("Category:"));
        cmbCategoryFilter = new JComboBox<>();
        cmbCategoryFilter.addItem("All Categories");
        searchPanel.add(cmbCategoryFilter);

        topPanel.add(searchPanel, BorderLayout.WEST);

        // Stats panel
        lblInventoryStats = new JLabel("Loading inventory statistics...");
        lblInventoryStats.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(lblInventoryStats, BorderLayout.EAST);

        tabInventory.add(topPanel, BorderLayout.NORTH);

        // Medications table
        String[] columnNames = {"ID", "Name", "Category", "Stock", "Min Stock", "Unit Price", "Dosage Form", "Status", "Expiry"};
        medicationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMedications = new JTable(medicationTableModel);
        tblMedications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMedications.getTableHeader().setReorderingAllowed(false);

        // Custom cell renderer for low stock highlighting
        tblMedications.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    // Highlight low stock items
                    Integer stock = (Integer) table.getValueAt(row, 3);
                    Integer minStock = (Integer) table.getValueAt(row, 4);
                    String status = (String) table.getValueAt(row, 7);

                    if (stock != null && minStock != null && stock <= minStock) {
                        c.setBackground(new Color(255, 235, 235)); // Light red
                    } else if ("Out of Stock".equals(status)) {
                        c.setBackground(new Color(255, 200, 200)); // Red
                    } else {
                        c.setBackground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblMedications);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Medication Inventory"));
        tabInventory.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddMedication = new JButton("➕ Add Medication");
        btnEditMedication = new JButton("✏️ Edit");
        btnDispense = new JButton("💊 Dispense");
        btnRestock = new JButton("📦 Restock");
        btnRefreshInventory = new JButton("🔄 Refresh");

        buttonPanel.add(btnAddMedication);
        buttonPanel.add(btnEditMedication);
        buttonPanel.add(btnDispense);
        buttonPanel.add(btnRestock);
        buttonPanel.add(btnRefreshInventory);

        tabInventory.add(buttonPanel, BorderLayout.SOUTH);

        return tabInventory;
    }

    private JPanel createPrescriptionsTab() {
        JPanel tabPrescriptions = new JPanel(new BorderLayout(5, 5));
        tabPrescriptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Status Filter:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Pending", "Filled", "Cancelled"});
        filterPanel.add(cmbStatusFilter);

        tabPrescriptions.add(filterPanel, BorderLayout.NORTH);

        // Prescriptions table
        String[] columnNames = {"ID", "Patient", "Doctor", "Prescribed Date", "Status", "Items", "Total Cost"};
        prescriptionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPrescriptions = new JTable(prescriptionTableModel);
        tblPrescriptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrescriptions.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tblPrescriptions);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Prescriptions"));
        tabPrescriptions.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFillPrescription = new JButton("💊 Fill Prescription");
        btnViewPrescription = new JButton("👁️ View Details");
        btnRefreshPrescriptions = new JButton("🔄 Refresh");

        buttonPanel.add(btnFillPrescription);
        buttonPanel.add(btnViewPrescription);
        buttonPanel.add(btnRefreshPrescriptions);

        tabPrescriptions.add(buttonPanel, BorderLayout.SOUTH);

        return tabPrescriptions;
    }

    private JPanel createReportsTab() {
        JPanel tabReports = new JPanel(new BorderLayout(5, 5));
        tabReports.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerateInventoryReport = new JButton("📊 Inventory Report");
        btnLowStockReport = new JButton("⚠️ Low Stock Report");
        btnExpiryReport = new JButton("📅 Expiry Report");
        btnExportReport = new JButton("📤 Export Report");

        buttonPanel.add(btnGenerateInventoryReport);
        buttonPanel.add(btnLowStockReport);
        buttonPanel.add(btnExpiryReport);
        buttonPanel.add(btnExportReport);

        tabReports.add(buttonPanel, BorderLayout.NORTH);

        // Report display area
        txtReportArea = new JTextArea();
        txtReportArea.setEditable(false);
        txtReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtReportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Report Output"));
        tabReports.add(scrollPane, BorderLayout.CENTER);

        return tabReports;
    }

    private void postInit(Staff user) {
        this.currentUser = user;
        setTitle("💊 Pharmacist Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);

        // Event handlers
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Pharmacist: " + (currentUser != null ? currentUser.getName() : "Unknown") + "\n"
                    + "Email: " + (currentUser != null ? currentUser.getEmail() : "Unknown"),
                    "Profile Information", JOptionPane.INFORMATION_MESSAGE);
        });

        // Inventory actions
        btnAddMedication.addActionListener(this::addMedicationAction);
        btnEditMedication.addActionListener(this::editMedicationAction);
        btnDispense.addActionListener(this::dispenseMedicationAction);
        btnRestock.addActionListener(this::restockMedicationAction);
        btnRefreshInventory.addActionListener(e -> loadMedicationsData());

        // Search and filter actions
        txtSearchMedication.addActionListener(e -> searchMedications());
        cmbCategoryFilter.addActionListener(e -> filterMedicationsByCategory());

        // Prescription actions
        btnFillPrescription.addActionListener(this::fillPrescriptionAction);
        btnViewPrescription.addActionListener(this::viewPrescriptionAction);
        btnRefreshPrescriptions.addActionListener(e -> loadPrescriptionsData());
        cmbStatusFilter.addActionListener(e -> filterPrescriptionsByStatus());

        // Report actions
        btnGenerateInventoryReport.addActionListener(this::generateInventoryReportAction);
        btnLowStockReport.addActionListener(this::generateLowStockReportAction);
        btnExpiryReport.addActionListener(this::generateExpiryReportAction);
        btnExportReport.addActionListener(this::exportReportAction);

        // Load initial data
        loadMedicationsData();
        loadPrescriptionsData();
        updateInventoryStats();
        loadCategoryFilter();
    }

    // ===== DATA LOADING METHODS =====
    private void loadMedicationsData() {
        try {
            List<Medication> medications = medicationDao.findAll();
            medicationTableModel.setRowCount(0);

            for (Medication med : medications) {
                Object[] row = {
                    med.getId(),
                    med.getName(),
                    med.getCategory(),
                    med.getStockQuantity(),
                    med.getMinimumStock(),
                    String.format("$%.2f", med.getUnitPrice()),
                    med.getDosageForm() + (med.getStrength() != null ? " (" + med.getStrength() + ")" : ""),
                    med.getStatus(),
                    med.getExpiryDate() != null ? med.getExpiryDate().toString() : "N/A"
                };
                medicationTableModel.addRow(row);
            }

            updateInventoryStats();
            System.out.println("✅ Loaded " + medications.size() + " medications");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading medications: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadPrescriptionsData() {
        try {
            List<Prescription> prescriptions = prescriptionDao.findAll();
            prescriptionTableModel.setRowCount(0);

            for (Prescription prescription : prescriptions) {
                String patientName = prescription.getPatient() != null
                        ? prescription.getPatient().getFullName() : "Unknown";
                String doctorName = prescription.getDoctor() != null
                        ? prescription.getDoctor().getName() : "Unknown";
                String prescribedDate = prescription.getPrescribedDate() != null
                        ? prescription.getPrescribedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                int itemCount = prescription.getItems() != null ? prescription.getItems().size() : 0;

                Object[] row = {
                    prescription.getId(),
                    patientName,
                    doctorName,
                    prescribedDate,
                    prescription.getStatus(),
                    itemCount + " items",
                    String.format("$%.2f", prescription.getTotalCost())
                };
                prescriptionTableModel.addRow(row);
            }

            System.out.println("✅ Loaded " + prescriptions.size() + " prescriptions");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading prescriptions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateInventoryStats() {
        try {
            Map<String, Object> stats = pharmacistService.getPharmacyStatistics();
            String statsText = String.format(
                    "📊 Total: %s | 📦 Available: %s | ⚠️ Low Stock: %s | 🚨 Expired: %s | 💰 Value: $%.2f",
                    stats.get("totalMedications"),
                    stats.get("availableMedications"),
                    stats.get("lowStockMedications"),
                    stats.get("expiredMedications"),
                    stats.get("totalInventoryValue")
            );
            lblInventoryStats.setText(statsText);
        } catch (Exception e) {
            lblInventoryStats.setText("Error loading statistics");
        }
    }

    private void loadCategoryFilter() {
        try {
            List<String> categories = pharmacistService.getMedicationCategories();
            cmbCategoryFilter.removeAllItems();
            cmbCategoryFilter.addItem("All Categories");
            for (String category : categories) {
                cmbCategoryFilter.addItem(category);
            }
        } catch (Exception e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }
    // ===== SEARCH AND FILTER METHODS =====

    private void searchMedications() {
        String searchTerm = txtSearchMedication.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadMedicationsData();
            return;
        }

        try {
            List<Medication> allMedications = medicationDao.findAll();
            medicationTableModel.setRowCount(0);

            for (Medication med : allMedications) {
                if (med.getName().toLowerCase().contains(searchTerm)
                        || med.getCategory().toLowerCase().contains(searchTerm)
                        || med.getManufacturer().toLowerCase().contains(searchTerm)) {

                    Object[] row = {
                        med.getId(),
                        med.getName(),
                        med.getCategory(),
                        med.getStockQuantity(),
                        med.getMinimumStock(),
                        String.format("$%.2f", med.getUnitPrice()),
                        med.getDosageForm() + (med.getStrength() != null ? " (" + med.getStrength() + ")" : ""),
                        med.getStatus(),
                        med.getExpiryDate() != null ? med.getExpiryDate().toString() : "N/A"
                    };
                    medicationTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching medications: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterMedicationsByCategory() {
        String selectedCategory = (String) cmbCategoryFilter.getSelectedItem();
        if ("All Categories".equals(selectedCategory)) {
            loadMedicationsData();
            return;
        }

        try {
            List<Medication> allMedications = medicationDao.findAll();
            medicationTableModel.setRowCount(0);

            for (Medication med : allMedications) {
                if (med.getCategory().equals(selectedCategory)) {
                    Object[] row = {
                        med.getId(),
                        med.getName(),
                        med.getCategory(),
                        med.getStockQuantity(),
                        med.getMinimumStock(),
                        String.format("$%.2f", med.getUnitPrice()),
                        med.getDosageForm() + (med.getStrength() != null ? " (" + med.getStrength() + ")" : ""),
                        med.getStatus(),
                        med.getExpiryDate() != null ? med.getExpiryDate().toString() : "N/A"
                    };
                    medicationTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering medications: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterPrescriptionsByStatus() {
        String selectedStatus = (String) cmbStatusFilter.getSelectedItem();
        if ("All".equals(selectedStatus)) {
            loadPrescriptionsData();
            return;
        }

        try {
            List<Prescription> allPrescriptions = prescriptionDao.findAll();
            prescriptionTableModel.setRowCount(0);

            for (Prescription prescription : allPrescriptions) {
                if (prescription.getStatus().equals(selectedStatus)) {
                    String patientName = prescription.getPatient() != null
                            ? prescription.getPatient().getFullName() : "Unknown";
                    String doctorName = prescription.getDoctor() != null
                            ? prescription.getDoctor().getName() : "Unknown";
                    String prescribedDate = prescription.getPrescribedDate() != null
                            ? prescription.getPrescribedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                    int itemCount = prescription.getItems() != null ? prescription.getItems().size() : 0;

                    Object[] row = {
                        prescription.getId(),
                        patientName,
                        doctorName,
                        prescribedDate,
                        prescription.getStatus(),
                        itemCount + " items",
                        String.format("$%.2f", prescription.getTotalCost())
                    };
                    prescriptionTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering prescriptions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ===== ACTION METHODS =====

    private void addMedicationAction(ActionEvent e) {
        AddMedicationDialog dialog = new AddMedicationDialog(this);
        dialog.setVisible(true);

        Medication newMedication = dialog.getCreatedMedication();
        if (newMedication != null) {
            try {
                pharmacistService.addMedication(newMedication);
                loadMedicationsData();
                loadCategoryFilter();
                JOptionPane.showMessageDialog(this,
                        "Medication '" + newMedication.getName() + "' added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error adding medication: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMedicationAction(ActionEvent e) {
        int selectedRow = tblMedications.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medication to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long medicationId = (Long) medicationTableModel.getValueAt(selectedRow, 0);
        Medication medication = medicationDao.findById(medicationId);

        if (medication == null) {
            JOptionPane.showMessageDialog(this,
                    "Medication not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show medication details (can be expanded to edit dialog)
        String details = String.format(
                "Medication Details:\n\n"
                + "Name: %s\n"
                + "Category: %s\n"
                + "Manufacturer: %s\n"
                + "Stock: %d units\n"
                + "Minimum Stock: %d units\n"
                + "Unit Price: $%.2f\n"
                + "Dosage Form: %s\n"
                + "Strength: %s\n"
                + "Status: %s\n"
                + "Expiry Date: %s\n"
                + "Batch Number: %s",
                medication.getName(),
                medication.getCategory(),
                medication.getManufacturer(),
                medication.getStockQuantity(),
                medication.getMinimumStock(),
                medication.getUnitPrice(),
                medication.getDosageForm(),
                medication.getStrength() != null ? medication.getStrength() : "N/A",
                medication.getStatus(),
                medication.getExpiryDate() != null ? medication.getExpiryDate().toString() : "N/A",
                medication.getBatchNumber() != null ? medication.getBatchNumber() : "N/A"
        );

        JOptionPane.showMessageDialog(this, details, "Medication Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void dispenseMedicationAction(ActionEvent e) {
        int selectedRow = tblMedications.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medication to dispense.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long medicationId = (Long) medicationTableModel.getValueAt(selectedRow, 0);
        Medication medication = medicationDao.findById(medicationId);

        if (medication == null) {
            JOptionPane.showMessageDialog(this,
                    "Medication not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DispenseMedicationDialog dialog = new DispenseMedicationDialog(this, medication);
        dialog.setVisible(true);

        if (dialog.isDispensed()) {
            loadMedicationsData();
            updateInventoryStats();
        }
    }

    private void restockMedicationAction(ActionEvent e) {
        int selectedRow = tblMedications.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medication to restock.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long medicationId = (Long) medicationTableModel.getValueAt(selectedRow, 0);
        Medication medication = medicationDao.findById(medicationId);

        if (medication == null) {
            JOptionPane.showMessageDialog(this,
                    "Medication not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(this,
                "Enter quantity to add to stock for " + medication.getName() + ":",
                "Restock Medication",
                JOptionPane.QUESTION_MESSAGE);

        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a positive quantity.",
                            "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                medication.setStockQuantity(medication.getStockQuantity() + quantity);
                medicationDao.update(medication);

                loadMedicationsData();
                updateInventoryStats();

                JOptionPane.showMessageDialog(this,
                        "Successfully added " + quantity + " units to " + medication.getName()
                        + "\nNew stock: " + medication.getStockQuantity() + " units",
                        "Restock Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error restocking medication: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void fillPrescriptionAction(ActionEvent e) {
        int selectedRow = tblPrescriptions.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a prescription to fill.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long prescriptionId = (Long) prescriptionTableModel.getValueAt(selectedRow, 0);
        Prescription prescription = prescriptionDao.findById(prescriptionId);

        if (prescription == null) {
            JOptionPane.showMessageDialog(this,
                    "Prescription not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!"Pending".equals(prescription.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "This prescription has already been processed.",
                    "Already Processed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simple confirmation dialog for now
        int result = JOptionPane.showConfirmDialog(this,
                "Fill prescription for patient: " + prescription.getPatient().getFullName() + "?\n"
                + "Total cost: $" + String.format("%.2f", prescription.getTotalCost()),
                "Fill Prescription",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                prescription.setStatus("Filled");
                prescriptionDao.update(prescription);
                loadPrescriptionsData();

                JOptionPane.showMessageDialog(this,
                        "Prescription filled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error filling prescription: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewPrescriptionAction(ActionEvent e) {
        int selectedRow = tblPrescriptions.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a prescription to view.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long prescriptionId = (Long) prescriptionTableModel.getValueAt(selectedRow, 0);
        Prescription prescription = prescriptionDao.findById(prescriptionId);

        if (prescription == null) {
            JOptionPane.showMessageDialog(this,
                    "Prescription not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Prescription Details:\n\n");
        details.append("ID: ").append(prescription.getId()).append("\n");
        details.append("Patient: ").append(prescription.getPatient().getFullName()).append("\n");
        details.append("Doctor: ").append(prescription.getDoctor().getName()).append("\n");
        details.append("Prescribed Date: ").append(prescription.getPrescribedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        details.append("Status: ").append(prescription.getStatus()).append("\n");
        details.append("Total Cost: $").append(String.format("%.2f", prescription.getTotalCost())).append("\n\n");
        details.append("Items:\n");

        if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
            for (var item : prescription.getItems()) {
                details.append("- ").append(item.getMedication().getName())
                        .append(" (").append(item.getQuantity()).append(" units)")
                        .append(" - $").append(String.format("%.2f", item.getItemTotal())).append("\n");
            }
        } else {
            details.append("No items found.\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Prescription Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== REPORT GENERATION METHODS =====
    private void generateInventoryReportAction(ActionEvent e) {
        try {
            String report = pharmacistService.generateInventoryReport();
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating inventory report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateLowStockReportAction(ActionEvent e) {
        try {
            String report = pharmacistService.generateLowStockReport();
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating low stock report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateExpiryReportAction(ActionEvent e) {
        try {
            String report = pharmacistService.generateExpiryReport();
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating expiry report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReportAction(ActionEvent e) {
        if (txtReportArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No report to export. Please generate a report first.",
                    "No Report", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("pharmacy_report.txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(), txtReportArea.getText().getBytes());

                JOptionPane.showMessageDialog(this,
                        "Report exported successfully to: " + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting report: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
