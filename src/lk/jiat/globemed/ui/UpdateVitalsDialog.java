package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.NurseService;

public class UpdateVitalsDialog extends JDialog {

    private final NurseService nurseService = new NurseService();
    private final Patient patient;
    private final Staff nurse;

    private JTextField txtTemperature;
    private JTextField txtSystolicBP;
    private JTextField txtDiastolicBP;
    private JTextField txtHeartRate;
    private JTextField txtRespiratoryRate;
    private JTextField txtOxygenSaturation;
    private JTextField txtWeight;
    private JTextField txtHeight;
    private JTextArea txtNotes;
    private JButton btnSave;
    private JButton btnCancel;

    private boolean updated = false;

    public UpdateVitalsDialog(Frame parent, Patient patient, Staff nurse) {
        super(parent, "Update Patient Vitals", true);
        this.patient = patient;
        this.nurse = nurse;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(400, 500);
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        JLabel lblTitle = new JLabel("Update Vitals for: " + patient.getFullName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        titlePanel.add(lblTitle);
        add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Temperature (°C):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTemperature = new JTextField(10);
        formPanel.add(txtTemperature, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Systolic BP (mmHg):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSystolicBP = new JTextField(10);
        formPanel.add(txtSystolicBP, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Diastolic BP (mmHg):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDiastolicBP = new JTextField(10);
        formPanel.add(txtDiastolicBP, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Heart Rate (bpm):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtHeartRate = new JTextField(10);
        formPanel.add(txtHeartRate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Respiratory Rate (/min):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtRespiratoryRate = new JTextField(10);
        formPanel.add(txtRespiratoryRate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Oxygen Saturation (%):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtOxygenSaturation = new JTextField(10);
        formPanel.add(txtOxygenSaturation, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtWeight = new JTextField(10);
        formPanel.add(txtWeight, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Height (cm):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtHeight = new JTextField(10);
        formPanel.add(txtHeight, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtNotes = new JTextArea(3, 20);
        txtNotes.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollPane = new JScrollPane(txtNotes);
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnSave = new JButton("Save Vitals");
        btnCancel = new JButton("Cancel");

        btnSave.addActionListener(this::saveAction);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveAction(ActionEvent e) {

        try {

            Double temperature = parseDouble(txtTemperature.getText());
            Integer systolicBP = parseInt(txtSystolicBP.getText());
            Integer diastolicBP = parseInt(txtDiastolicBP.getText());
            Integer heartRate = parseInt(txtHeartRate.getText());
            Integer respiratoryRate = parseInt(txtRespiratoryRate.getText());
            Double oxygenSaturation = parseDouble(txtOxygenSaturation.getText());
            Double weight = parseDouble(txtWeight.getText());
            Double height = parseDouble(txtHeight.getText());
            String notes = txtNotes.getText().trim();

            nurseService.recordPatientVitals(patient, nurse, temperature, systolicBP, diastolicBP,
                    heartRate, respiratoryRate, oxygenSaturation, weight, height, notes);

            updated = true;
            JOptionPane.showMessageDialog(this,
                    "Patient vitals recorded successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error recording vitals: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + text);
        }
    }

    private Integer parseInt(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + text);
        }
    }

    public boolean isUpdated() {
        return updated;
    }
}
