package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.NurseService;

public class CreateTaskDialog extends JDialog {

    private final NurseService nurseService = new NurseService();
    private final PatientDao patientDao = new PatientDao();
    private final Staff currentUser;

    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JComboBox<Patient> cmbPatient;
    private JComboBox<String> cmbPriority;
    private JComboBox<String> cmbTaskType;
    private JTextField txtDueDate;
    private JButton btnCreate;
    private JButton btnCancel;

    private boolean created = false;

    public CreateTaskDialog(Frame parent, Staff currentUser) {
        super(parent, "Create Nursing Task", true);
        this.currentUser = currentUser;
        initComponents();
        loadPatients();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 400);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTitle = new JTextField(20);
        formPanel.add(txtTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtDescription = new JTextArea(3, 20);
        txtDescription.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        formPanel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
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
                } else if (value == null) {
                    setText("General Task (No specific patient)");
                }
                return this;
            }
        });
        formPanel.add(cmbPatient, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbPriority = new JComboBox<>(new String[]{"Low", "Medium", "High", "Critical"});
        cmbPriority.setSelectedItem("Medium");
        formPanel.add(cmbPriority, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Task Type:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTaskType = new JComboBox<>(new String[]{"Medication", "Vitals", "Assessment", "Care", "Other"});
        formPanel.add(cmbTaskType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Due Date (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDueDate = new JTextField(20);
        txtDueDate.setText(LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        formPanel.add(txtDueDate, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCreate = new JButton("Create Task");
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
            cmbPatient.addItem(null); // For general tasks
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
            String title = txtTitle.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a task title.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String description = txtDescription.getText().trim();
            Patient selectedPatient = (Patient) cmbPatient.getSelectedItem();
            String priority = (String) cmbPriority.getSelectedItem();
            String taskType = (String) cmbTaskType.getSelectedItem();

            LocalDateTime dueDateTime = null;
            String dueDateStr = txtDueDate.getText().trim();
            if (!dueDateStr.isEmpty()) {
                try {
                    dueDateTime = LocalDateTime.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format. Please use yyyy-MM-dd HH:mm",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            nurseService.createTask(title, description, selectedPatient, currentUser, currentUser,
                    priority, taskType, dueDateTime);

            created = true;
            JOptionPane.showMessageDialog(this,
                    "Task created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error creating task: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCreated() {
        return created;
    }
}
