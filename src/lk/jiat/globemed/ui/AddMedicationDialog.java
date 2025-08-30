package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import lk.jiat.globemed.model.Medication;

public class AddMedicationDialog extends JDialog {

    private Medication createdMedication;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JComboBox<String> cmbCategory;
    private JTextField txtManufacturer;
    private JTextField txtUnitPrice;
    private JTextField txtStockQuantity;
    private JTextField txtMinimumStock;
    private JComboBox<String> cmbDosageForm;
    private JTextField txtStrength;
    private JTextField txtExpiryDate;
    private JTextField txtBatchNumber;
    private JComboBox<String> cmbStatus;
    private JButton btnSave;
    private JButton btnCancel;

    public AddMedicationDialog(Frame parent) {
        super(parent, "Add New Medication", true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(500, 600);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblName = new JLabel("Medication Name :");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblName, gbc);
        txtName = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(txtName, gbc);

        JLabel lblDescription = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lblDescription, gbc);
        txtDescription = new JTextArea(3, 25);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(scrollDesc, gbc);

        JLabel lblCategory = new JLabel("Category :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(lblCategory, gbc);
        cmbCategory = new JComboBox<>(new String[]{
            "Antibiotics", "Pain Relief", "Vitamins", "Cardiovascular",
            "Respiratory", "Digestive", "Neurological", "Dermatological", "Other"
        });
        cmbCategory.setEditable(true);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(cmbCategory, gbc);

        JLabel lblManufacturer = new JLabel("Manufacturer :");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(lblManufacturer, gbc);
        txtManufacturer = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(txtManufacturer, gbc);

        JLabel lblUnitPrice = new JLabel("Unit Price (LKR) :");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(lblUnitPrice, gbc);
        txtUnitPrice = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(txtUnitPrice, gbc);

        JLabel lblStockQuantity = new JLabel("Stock Quantity :");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(lblStockQuantity, gbc);
        txtStockQuantity = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(txtStockQuantity, gbc);

        JLabel lblMinimumStock = new JLabel("Minimum Stock :");
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(lblMinimumStock, gbc);
        txtMinimumStock = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 6;
        mainPanel.add(txtMinimumStock, gbc);

        JLabel lblDosageForm = new JLabel("Dosage Form :");
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(lblDosageForm, gbc);
        cmbDosageForm = new JComboBox<>(new String[]{
            "Tablet", "Capsule", "Syrup", "Injection", "Cream", "Ointment", "Drops", "Inhaler"
        });
        cmbDosageForm.setEditable(true);
        gbc.gridx = 1;
        gbc.gridy = 7;
        mainPanel.add(cmbDosageForm, gbc);

        JLabel lblStrength = new JLabel("Strength:");
        gbc.gridx = 0;
        gbc.gridy = 8;
        mainPanel.add(lblStrength, gbc);
        txtStrength = new JTextField(25);
        txtStrength.setToolTipText("e.g., 500mg, 10ml, 250mcg");
        gbc.gridx = 1;
        gbc.gridy = 8;
        mainPanel.add(txtStrength, gbc);

        JLabel lblExpiryDate = new JLabel("Expiry Date:");
        gbc.gridx = 0;
        gbc.gridy = 9;
        mainPanel.add(lblExpiryDate, gbc);
        txtExpiryDate = new JTextField(25);
        txtExpiryDate.setToolTipText("Format: YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 9;
        mainPanel.add(txtExpiryDate, gbc);

        JLabel lblBatchNumber = new JLabel("Batch Number:");
        gbc.gridx = 0;
        gbc.gridy = 10;
        mainPanel.add(lblBatchNumber, gbc);
        txtBatchNumber = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 10;
        mainPanel.add(txtBatchNumber, gbc);

        JLabel lblStatus = new JLabel("Status:");
        gbc.gridx = 0;
        gbc.gridy = 11;
        mainPanel.add(lblStatus, gbc);
        cmbStatus = new JComboBox<>(new String[]{
            "Available", "Out of Stock", "Discontinued"
        });
        gbc.gridx = 1;
        gbc.gridy = 11;
        mainPanel.add(cmbStatus, gbc);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("💾 Save");
        btnCancel = new JButton("❌ Cancel");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(this::onSave);
        btnCancel.addActionListener(this::onCancel);

        cmbStatus.setSelectedItem("Available");
    }

    private void onSave(ActionEvent e) {
        try {

            if (txtName.getText().trim().isEmpty()) {
                showError("Medication name is required.");
                return;
            }

            if (cmbCategory.getSelectedItem() == null || cmbCategory.getSelectedItem().toString().trim().isEmpty()) {
                showError("Category is required.");
                return;
            }

            if (txtManufacturer.getText().trim().isEmpty()) {
                showError("Manufacturer is required.");
                return;
            }

            if (txtUnitPrice.getText().trim().isEmpty()) {
                showError("Unit price is required.");
                return;
            }

            if (txtStockQuantity.getText().trim().isEmpty()) {
                showError("Stock quantity is required.");
                return;
            }

            if (txtMinimumStock.getText().trim().isEmpty()) {
                showError("Minimum stock is required.");
                return;
            }

            if (cmbDosageForm.getSelectedItem() == null || cmbDosageForm.getSelectedItem().toString().trim().isEmpty()) {
                showError("Dosage form is required.");
                return;
            }

            double unitPrice;
            int stockQuantity;
            int minimumStock;

            try {
                unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());
                if (unitPrice < 0) {
                    showError("Unit price must be positive.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Invalid unit price format.");
                return;
            }

            try {
                stockQuantity = Integer.parseInt(txtStockQuantity.getText().trim());
                if (stockQuantity < 0) {
                    showError("Stock quantity must be non-negative.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Invalid stock quantity format.");
                return;
            }

            try {
                minimumStock = Integer.parseInt(txtMinimumStock.getText().trim());
                if (minimumStock < 0) {
                    showError("Minimum stock must be non-negative.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Invalid minimum stock format.");
                return;
            }

            LocalDate expiryDate = null;
            if (!txtExpiryDate.getText().trim().isEmpty()) {
                try {
                    expiryDate = LocalDate.parse(txtExpiryDate.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    showError("Invalid expiry date format. Use YYYY-MM-DD.");
                    return;
                }
            }

            Medication medication = new Medication();
            medication.setName(txtName.getText().trim());
            medication.setDescription(txtDescription.getText().trim());
            medication.setCategory(cmbCategory.getSelectedItem().toString().trim());
            medication.setManufacturer(txtManufacturer.getText().trim());
            medication.setUnitPrice(unitPrice);
            medication.setStockQuantity(stockQuantity);
            medication.setMinimumStock(minimumStock);
            medication.setDosageForm(cmbDosageForm.getSelectedItem().toString().trim());
            medication.setStrength(txtStrength.getText().trim());
            medication.setExpiryDate(expiryDate);
            medication.setBatchNumber(txtBatchNumber.getText().trim());
            medication.setStatus(cmbStatus.getSelectedItem().toString());

            this.createdMedication = medication;
            dispose();

        } catch (Exception ex) {
            showError("Error creating medication: " + ex.getMessage());
        }
    }

    private void onCancel(ActionEvent e) {
        this.createdMedication = null;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public Medication getCreatedMedication() {
        return createdMedication;
    }
}
