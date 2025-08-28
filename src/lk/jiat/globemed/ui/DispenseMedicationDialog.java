package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Medication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DispenseMedicationDialog extends JDialog {

    private boolean dispensed = false;
    private int dispensedQuantity = 0;
    private Medication medication;
    private JLabel lblMedicationInfo;
    private JLabel lblCurrentStock;
    private JTextField txtQuantity;
    private JTextArea txtNotes;
    private JButton btnDispense;
    private JButton btnCancel;

    public DispenseMedicationDialog(Frame parent, Medication medication) {
        super(parent, "Dispense Medication", true);
        this.medication = medication;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 350);
        setLayout(new BorderLayout(10, 10));

        // Header panel with medication info
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Medication Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        lblMedicationInfo = new JLabel("<html><b>" + medication.getName() + "</b><br/>" +
                "Form: " + medication.getDosageForm() + 
                (medication.getStrength() != null ? " | Strength: " + medication.getStrength() : "") + 
                "<br/>Category: " + medication.getCategory() + "</html>");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        headerPanel.add(lblMedicationInfo, gbc);

        lblCurrentStock = new JLabel("Current Stock: " + medication.getStockQuantity() + " units");
        lblCurrentStock.setFont(lblCurrentStock.getFont().deriveFont(Font.BOLD));
        if (medication.isLowStock()) {
            lblCurrentStock.setForeground(Color.RED);
            lblCurrentStock.setText(lblCurrentStock.getText() + " (LOW STOCK!)");
        }
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        headerPanel.add(lblCurrentStock, gbc);

        add(headerPanel, BorderLayout.NORTH);

        // Main panel for dispense details
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Dispense Details"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Quantity to dispense
        JLabel lblQuantity = new JLabel("Quantity to Dispense:");
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(lblQuantity, gbc);

        txtQuantity = new JTextField(10);
        txtQuantity.setToolTipText("Enter the number of units to dispense");
        gbc.gridx = 1; gbc.gridy = 0;
        mainPanel.add(txtQuantity, gbc);

        JLabel lblUnits = new JLabel("units");
        gbc.gridx = 2; gbc.gridy = 0;
        mainPanel.add(lblUnits, gbc);

        // Notes
        JLabel lblNotes = new JLabel("Notes (Optional):");
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(lblNotes, gbc);

        txtNotes = new JTextArea(4, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setToolTipText("Enter any additional notes about this dispensation");
        JScrollPane scrollNotes = new JScrollPane(txtNotes);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollNotes, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDispense = new JButton("💊 Dispense");
        btnCancel = new JButton("❌ Cancel");
        
        btnDispense.setBackground(new Color(46, 125, 50));
        btnDispense.setForeground(Color.WHITE);
        btnDispense.setFocusPainted(false);
        
        buttonPanel.add(btnDispense);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers
        btnDispense.addActionListener(this::onDispense);
        btnCancel.addActionListener(this::onCancel);

        // Focus on quantity field
        txtQuantity.requestFocus();
    }

    private void onDispense(ActionEvent e) {
        try {
            // Validate quantity
            if (txtQuantity.getText().trim().isEmpty()) {
                showError("Please enter the quantity to dispense.");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(txtQuantity.getText().trim());
                if (quantity <= 0) {
                    showError("Quantity must be a positive number.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Invalid quantity format. Please enter a valid number.");
                return;
            }

            // Check if sufficient stock is available
            if (quantity > medication.getStockQuantity()) {
                showError("Insufficient stock! Available: " + medication.getStockQuantity() + 
                         " units, Requested: " + quantity + " units.");
                return;
            }

            // Confirm dispensation
            String confirmMessage = "Dispense " + quantity + " units of " + medication.getName() + "?\n\n" +
                    "Current Stock: " + medication.getStockQuantity() + " units\n" +
                    "After Dispensation: " + (medication.getStockQuantity() - quantity) + " units";

            if (medication.getStockQuantity() - quantity <= medication.getMinimumStock()) {
                confirmMessage += "\n\n⚠️ WARNING: This will result in low stock!";
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                confirmMessage, 
                "Confirm Dispensation", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispensedQuantity = quantity;
                this.dispensed = true;
                dispose();
            }

        } catch (Exception ex) {
            showError("Error processing dispensation: " + ex.getMessage());
        }
    }

    private void onCancel(ActionEvent e) {
        this.dispensed = false;
        this.dispensedQuantity = 0;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isDispensed() {
        return dispensed;
    }

    public int getDispensedQuantity() {
        return dispensedQuantity;
    }

    public String getNotes() {
        return txtNotes.getText().trim();
    }
}