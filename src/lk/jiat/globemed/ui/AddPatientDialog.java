package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.service.PatientService;

public class AddPatientDialog extends JDialog {

    private final PatientService patientService = new PatientService();

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtDateOfBirth; // yyyy-MM-dd format
    private JComboBox<String> cmbGender;
    private JTextField txtEmergencyContact;
    private JTextArea txtMedicalHistory;

    private boolean saved = false;

    public AddPatientDialog(Frame parent) {
        super(parent, "Add New Patient", true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(500, 600);

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel lblTitle = new JLabel("Patient Registration");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        titlePanel.add(lblTitle);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(" First Name: "), gbc);
        gbc.gridx = 1;
        txtFirstName = new JTextField(20);
        formPanel.add(txtFirstName, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(" Last Name: "), gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(20);
        formPanel.add(txtLastName, gbc);

        // Email
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        formPanel.add(new JLabel(" Email: "), gbc);
//        gbc.gridx = 1;
//        txtEmail = new JTextField(20);
//        formPanel.add(txtEmail, gbc);
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel(" Phone: "), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        formPanel.add(txtPhone, gbc);

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel(" Date of Birth (yyyy-MM-dd): "), gbc);
        gbc.gridx = 1;
        txtDateOfBirth = new JTextField(20);
        formPanel.add(txtDateOfBirth, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel(" Gender: "), gbc);
        gbc.gridx = 1;
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(cmbGender, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel(" Address: "), gbc);
        gbc.gridx = 1;
        txtAddress = new JTextField(20);
        formPanel.add(txtAddress, gbc);

//        // Emergency Contact
//        gbc.gridx = 0;
//        gbc.gridy = 7;
//        formPanel.add(new JLabel(" Emergency Contact: "), gbc);
//        gbc.gridx = 1;
//        txtEmergencyContact = new JTextField(20);
//        formPanel.add(txtEmergencyContact, gbc);
//
//        // Medical History
//        gbc.gridx = 0;
//        gbc.gridy = 8;
//        gbc.anchor = GridBagConstraints.NORTHWEST;
//        formPanel.add(new JLabel("Medical History:"), gbc);
//        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.weightx = 1.0;
//        gbc.weighty = 1.0;
//        txtMedicalHistory = new JTextArea(4, 20);
//        txtMedicalHistory.setLineWrap(true);
//        txtMedicalHistory.setWrapStyleWord(true);
//        JScrollPane scrollHistory = new JScrollPane(txtMedicalHistory);
//        formPanel.add(scrollHistory, gbc);
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSave = new JButton("Save Patient");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(this::onSave);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // Add panels to dialog
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent e) {
        try {
            // Validate required fields
            if (txtFirstName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name is required");
                return;
            }

            if (txtLastName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Last Name is required");
                return;
            }

            if (txtPhone.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number is required");
                return;
            }

            if (txtDateOfBirth.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Date of Birth is required");
                return;
            }

            // Validate date format
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.parse(txtDateOfBirth.getText().trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use yyyy-MM-dd (e.g., 1990-05-15)");
                return;
            }

            // Create patient object
            Patient patient = new Patient();
            patient.setFirstName(txtFirstName.getText().trim());
            patient.setLastName(txtLastName.getText().trim());
            patient.setContactNumber(txtPhone.getText().trim());
            patient.setDob(dateOfBirth);
            patient.setGender((String) cmbGender.getSelectedItem());
            patient.setAddress(txtAddress.getText().trim());

            // Save patient
            Patient savedPatient = patientService.create(patient);

            JOptionPane.showMessageDialog(this,
                    "Patient registered successfully!\nPatient ID: " + savedPatient.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            saved = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving patient: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
