package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.service.AccountantService;

public class CreateBillingDialog extends JDialog {

    private final AccountantService accountantService = new AccountantService();
    private final PatientDao patientDao = new PatientDao();

    private JComboBox<Patient> cmbPatient;
    private JTextField txtAmount;
    private JComboBox<String> cmbPaymentMethod;
    private JTextArea txtDescription;
    private JButton btnCreate;
    private JButton btnCancel;

    private boolean created = false;

    public CreateBillingDialog(Frame parent) {
        super(parent, "Create Billing Record", true);
        initComponents();
        loadPatients();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbPatient = new JComboBox<>();
        cmbPatient.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    setText(((Patient) value).getFullName());
                }
                return this;
            }
        });
        formPanel.add(cmbPatient, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Amount ($):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtAmount = new JTextField();
        formPanel.add(txtAmount, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Card", "Insurance", "Bank Transfer"});
        formPanel.add(cmbPaymentMethod, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(3, 20);
        txtDescription.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCreate = new JButton("Create");
        btnCancel = new JButton("Cancel");

        btnCreate.addActionListener(this::createAction);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientDao.findAll();
            cmbPatient.removeAllItems();
            for (Patient patient : patients) {
                cmbPatient.addItem(patient);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading patients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAction(ActionEvent e) {

        try {

            Patient selectedPatient = (Patient) cmbPatient.getSelectedItem();
            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a patient.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter an amount.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    throw new NumberFormatException("Amount must be positive");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid positive amount.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();
            String description = txtDescription.getText().trim();

            accountantService.createBillingRecord(selectedPatient, amount, paymentMethod, description);

            created = true;
            JOptionPane.showMessageDialog(this,
                    "Billing record created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error creating billing record: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCreated() {
        return created;
    }
}
