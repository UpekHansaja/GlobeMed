package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lk.jiat.globemed.dao.AppointmentDao;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.NursingTask;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.PatientVitals;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.NurseService;

public class NurseDashboardForm extends JFrame {

    private Staff currentUser;
    private final NurseService nurseService = new NurseService();
    private final PatientDao patientDao = new PatientDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();

    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnProfile;
    private JTabbedPane tabMain;

    private JTable tblPatients;
    private DefaultTableModel patientsTableModel;
    private JButton btnUpdateVitals;
    private JButton btnViewVitalHistory;
    private JButton btnRefreshPatients;
    private JTextField txtSearchPatients;
    private JLabel lblPatientStats;

    private JTable tblTasks;
    private DefaultTableModel tasksTableModel;
    private JButton btnCompleteTask;
    private JButton btnStartTask;
    private JButton btnCreateTask;
    private JButton btnRefreshTasks;
    private JComboBox<String> cmbTaskStatusFilter;
    private JComboBox<String> cmbTaskPriorityFilter;

    private JTable tblAppointments;
    private DefaultTableModel appointmentsTableModel;
    private JButton btnUpdateAppointmentStatus;
    private JButton btnViewAppointmentDetails;
    private JButton btnRefreshAppointments;
    private JComboBox<String> cmbAppointmentFilter;

    private JTextArea txtReportArea;
    private JButton btnGenerateNursingReport;
    private JButton btnGenerateVitalsReport;
    private JButton btnExportReport;

    public NurseDashboardForm() {
        initComponents();
        postInit(null);
    }

    public NurseDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void initComponents() {

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        lblTitle = new JLabel("🩺 Nurse Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnProfile = new JButton("Profile");
        btnLogout = new JButton("Logout");
        headerRight.add(btnProfile);
        headerRight.add(btnLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(headerPanel, BorderLayout.NORTH);

        tabMain = new JTabbedPane();

        tabMain.addTab("Patients", createPatientsTab());
        tabMain.addTab("Tasks", createTasksTab());
        tabMain.addTab("Appointments", createAppointmentsTab());
        tabMain.addTab("Reports", createReportsTab());

        add(tabMain, BorderLayout.CENTER);
    }

    private JPanel createPatientsTab() {

        JPanel tabPatients = new JPanel(new BorderLayout(5, 5));
        tabPatients.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("🔍 Search:"));
        txtSearchPatients = new JTextField(15);
        txtSearchPatients.setToolTipText("Search patients by name");
        searchPanel.add(txtSearchPatients);

        topPanel.add(searchPanel, BorderLayout.WEST);

        lblPatientStats = new JLabel("Loading patient statistics...");
        lblPatientStats.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(lblPatientStats, BorderLayout.EAST);

        tabPatients.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Age", "Gender", "Contact", "Last Vitals", "Status"};
        patientsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPatients = new JTable(patientsTableModel);
        tblPatients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPatients.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tblPatients);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Patient List"));
        tabPatients.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUpdateVitals = new JButton("🩺 Update Vitals");
        btnViewVitalHistory = new JButton("📈 Vital History");
        btnRefreshPatients = new JButton("🔄 Refresh");

        buttonPanel.add(btnUpdateVitals);
        buttonPanel.add(btnViewVitalHistory);
        buttonPanel.add(btnRefreshPatients);

        tabPatients.add(buttonPanel, BorderLayout.SOUTH);

        return tabPatients;
    }

    private JPanel createTasksTab() {

        JPanel tabTasks = new JPanel(new BorderLayout(5, 5));
        tabTasks.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Status:"));
        cmbTaskStatusFilter = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Completed", "Cancelled"});
        filterPanel.add(cmbTaskStatusFilter);

        filterPanel.add(new JLabel("Priority:"));
        cmbTaskPriorityFilter = new JComboBox<>(new String[]{"All", "Low", "Medium", "High", "Critical"});
        filterPanel.add(cmbTaskPriorityFilter);

        tabTasks.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Title", "Patient", "Priority", "Status", "Due Date", "Type"};
        tasksTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTasks = new JTable(tasksTableModel);
        tblTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTasks.getTableHeader().setReorderingAllowed(false);

        tblTasks.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String priority = (String) table.getValueAt(row, 3);
                    String status = (String) table.getValueAt(row, 4);

                    if ("Critical".equals(priority) || "High".equals(priority)) {
                        c.setBackground(new Color(99, 3, 3)); // Dark red
                    } else if ("Completed".equals(status)) {
                        c.setBackground(new Color(4, 89, 46)); // Dark green
                    } else if ("In Progress".equals(status)) {
                        c.setBackground(new Color(128, 97, 6)); // Dark yellow
                    } else {
                        c.setBackground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblTasks);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Nursing Tasks"));
        tabTasks.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCompleteTask = new JButton("✅ Complete Task");
        btnStartTask = new JButton("▶️ Start Task");
        btnCreateTask = new JButton("➕ Create Task");
        btnRefreshTasks = new JButton("🔄 Refresh");

        buttonPanel.add(btnCompleteTask);
        buttonPanel.add(btnStartTask);
        buttonPanel.add(btnCreateTask);
        buttonPanel.add(btnRefreshTasks);

        tabTasks.add(buttonPanel, BorderLayout.SOUTH);

        return tabTasks;
    }

    private JPanel createAppointmentsTab() {

        JPanel tabAppointments = new JPanel(new BorderLayout(5, 5));
        tabAppointments.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        cmbAppointmentFilter = new JComboBox<>(new String[]{"All", "Today", "Scheduled", "Completed", "Cancelled"});
        filterPanel.add(cmbAppointmentFilter);

        tabAppointments.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Patient", "Doctor", "Date & Time", "Status"};
        appointmentsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAppointments = new JTable(appointmentsTableModel);
        tblAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAppointments.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tblAppointments);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        tabAppointments.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUpdateAppointmentStatus = new JButton("📝 Update Status");
        btnViewAppointmentDetails = new JButton("👁️ View Details");
        btnRefreshAppointments = new JButton("🔄 Refresh");

        buttonPanel.add(btnUpdateAppointmentStatus);
        buttonPanel.add(btnViewAppointmentDetails);
        buttonPanel.add(btnRefreshAppointments);

        tabAppointments.add(buttonPanel, BorderLayout.SOUTH);

        return tabAppointments;
    }

    private JPanel createReportsTab() {

        JPanel tabReports = new JPanel(new BorderLayout(5, 5));
        tabReports.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerateNursingReport = new JButton("📊 Nursing Report");
        btnGenerateVitalsReport = new JButton("🩺 Vitals Report");
        btnExportReport = new JButton("📤 Export Report");

        buttonPanel.add(btnGenerateNursingReport);
        buttonPanel.add(btnGenerateVitalsReport);
        buttonPanel.add(btnExportReport);

        tabReports.add(buttonPanel, BorderLayout.NORTH);

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
        setTitle("🩺 Nurse Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Nurse: " + (currentUser != null ? currentUser.getName() : "Unknown") + "\n"
                    + "Email: " + (currentUser != null ? currentUser.getEmail() : "Unknown"),
                    "Profile Information", JOptionPane.INFORMATION_MESSAGE);
        });

        btnUpdateVitals.addActionListener(this::updateVitalsAction);
        btnViewVitalHistory.addActionListener(this::viewVitalHistoryAction);
        btnRefreshPatients.addActionListener(e -> loadPatientsData());

        txtSearchPatients.addActionListener(e -> searchPatients());

        btnCompleteTask.addActionListener(this::completeTaskAction);
        btnStartTask.addActionListener(this::startTaskAction);
        btnCreateTask.addActionListener(this::createTaskAction);
        btnRefreshTasks.addActionListener(e -> loadTasksData());

        cmbTaskStatusFilter.addActionListener(e -> filterTasksByStatus());
        cmbTaskPriorityFilter.addActionListener(e -> filterTasksByPriority());

        btnUpdateAppointmentStatus.addActionListener(this::updateAppointmentStatusAction);
        btnViewAppointmentDetails.addActionListener(this::viewAppointmentDetailsAction);
        btnRefreshAppointments.addActionListener(e -> loadAppointmentsData());
        cmbAppointmentFilter.addActionListener(e -> filterAppointments());

        btnGenerateNursingReport.addActionListener(this::generateNursingReportAction);
        btnGenerateVitalsReport.addActionListener(this::generateVitalsReportAction);
        btnExportReport.addActionListener(this::exportReportAction);

        loadPatientsData();
        loadTasksData();
        loadAppointmentsData();
        updatePatientStats();
    }

    private void loadPatientsData() {

        try {
            List<Patient> patients = nurseService.getAllPatients();
            patientsTableModel.setRowCount(0);

            for (Patient patient : patients) {
                PatientVitals latestVitals = nurseService.getLatestVitals(patient.getId());
                String lastVitals = latestVitals != null
                        ? latestVitals.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "None";

                int age = patient.getDob() != null
                        ? java.time.Period.between(patient.getDob(), java.time.LocalDate.now()).getYears() : 0;

                Object[] row = {
                    patient.getId(),
                    patient.getFullName(),
                    age > 0 ? age : "N/A",
                    patient.getGender() != null ? patient.getGender() : "N/A",
                    patient.getContactNumber() != null ? patient.getContactNumber() : "N/A",
                    lastVitals,
                    "Active"
                };
                patientsTableModel.addRow(row);
            }

            updatePatientStats();
            System.out.println("Loaded " + patients.size() + " patients");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading patients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTasksData() {
        try {
            List<NursingTask> tasks = currentUser != null
                    ? nurseService.getTasksForNurse(currentUser.getId())
                    : List.of();

            tasksTableModel.setRowCount(0);

            for (NursingTask task : tasks) {
                Object[] row = {
                    task.getId(),
                    task.getTitle(),
                    task.getPatient() != null ? task.getPatient().getFullName() : "General",
                    task.getPriority(),
                    task.getStatus(),
                    task.getDueDateTime() != null
                    ? task.getDueDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                    task.getTaskType()
                };
                tasksTableModel.addRow(row);
            }

            System.out.println("Loaded " + tasks.size() + " tasks");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading tasks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadAppointmentsData() {

        try {
            List<Appointment> appointments = nurseService.getTodaysAppointments();
            appointmentsTableModel.setRowCount(0);

            for (Appointment appointment : appointments) {
                Object[] row = {
                    appointment.getId(),
                    appointment.getPatient().getFullName(),
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    appointment.getStatus()
                };
                appointmentsTableModel.addRow(row);
            }

            System.out.println("Loaded " + appointments.size() + " appointments");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading appointments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updatePatientStats() {

        try {
            if (currentUser != null) {
                Map<String, Object> stats = nurseService.getNursingStatistics(currentUser.getId());
                String statsText = String.format(
                        "Tasks: %s | Pending: %s | In Progress: %s | Completed: %s | Overdue: %s",
                        stats.get("totalTasks"),
                        stats.get("pendingTasks"),
                        stats.get("inProgressTasks"),
                        stats.get("completedTasks"),
                        stats.get("overdueTasks")
                );
                lblPatientStats.setText(statsText);
            }
        } catch (Exception e) {
            lblPatientStats.setText("Error loading statistics");
        }
    }

    private void searchPatients() {

        String searchTerm = txtSearchPatients.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPatientsData();
            return;
        }

        try {
            List<Patient> filteredPatients = nurseService.searchPatients(searchTerm);
            patientsTableModel.setRowCount(0);

            for (Patient patient : filteredPatients) {
                PatientVitals latestVitals = nurseService.getLatestVitals(patient.getId());
                String lastVitals = latestVitals != null
                        ? latestVitals.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "None";

                int age = patient.getDob() != null
                        ? java.time.Period.between(patient.getDob(), java.time.LocalDate.now()).getYears() : 0;

                Object[] row = {
                    patient.getId(),
                    patient.getFullName(),
                    age > 0 ? age : "N/A",
                    patient.getGender() != null ? patient.getGender() : "N/A",
                    patient.getContactNumber() != null ? patient.getContactNumber() : "N/A",
                    lastVitals,
                    "Active"
                };
                patientsTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching patients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTasksByStatus() {
        String selectedStatus = (String) cmbTaskStatusFilter.getSelectedItem();
        if ("All".equals(selectedStatus)) {
            loadTasksData();
            return;
        }

        try {
            List<NursingTask> allTasks = currentUser != null
                    ? nurseService.getTasksForNurse(currentUser.getId()) : List.of();

            tasksTableModel.setRowCount(0);

            for (NursingTask task : allTasks) {
                if (task.getStatus().equals(selectedStatus)) {
                    Object[] row = {
                        task.getId(),
                        task.getTitle(),
                        task.getPatient() != null ? task.getPatient().getFullName() : "General",
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDateTime() != null
                        ? task.getDueDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                        task.getTaskType()
                    };
                    tasksTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering tasks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTasksByPriority() {
        String selectedPriority = (String) cmbTaskPriorityFilter.getSelectedItem();
        if ("All".equals(selectedPriority)) {
            loadTasksData();
            return;
        }

        try {
            List<NursingTask> allTasks = currentUser != null
                    ? nurseService.getTasksForNurse(currentUser.getId()) : List.of();

            tasksTableModel.setRowCount(0);

            for (NursingTask task : allTasks) {
                if (task.getPriority().equals(selectedPriority)) {
                    Object[] row = {
                        task.getId(),
                        task.getTitle(),
                        task.getPatient() != null ? task.getPatient().getFullName() : "General",
                        task.getPriority(),
                        task.getStatus(),
                        task.getDueDateTime() != null
                        ? task.getDueDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                        task.getTaskType()
                    };
                    tasksTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering tasks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterAppointments() {
        String selectedFilter = (String) cmbAppointmentFilter.getSelectedItem();

        try {
            List<Appointment> appointments;

            switch (selectedFilter) {
                case "Today":
                    appointments = nurseService.getTodaysAppointments();
                    break;
                case "Scheduled":
                    appointments = nurseService.getScheduledAppointments();
                    break;
                default:
                    appointments = appointmentDao.findAll();
                    break;
            }

            appointmentsTableModel.setRowCount(0);

            for (Appointment appointment : appointments) {
                if ("All".equals(selectedFilter)
                        || appointment.getStatus().equals(selectedFilter)
                        || "Today".equals(selectedFilter)
                        || "Scheduled".equals(selectedFilter)) {

                    Object[] row = {
                        appointment.getId(),
                        appointment.getPatient().getFullName(),
                        appointment.getDoctor().getName(),
                        appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        appointment.getStatus()
                    };
                    appointmentsTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering appointments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVitalsAction(ActionEvent e) {

        int selectedRow = tblPatients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient to update vitals.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long patientId = (Long) patientsTableModel.getValueAt(selectedRow, 0);
        Patient patient = patientDao.findById(patientId);

        if (patient == null) {
            JOptionPane.showMessageDialog(this,
                    "Patient not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UpdateVitalsDialog dialog = new UpdateVitalsDialog(this, patient, currentUser);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            loadPatientsData();
            updatePatientStats();
        }
    }

    private void viewVitalHistoryAction(ActionEvent e) {

        int selectedRow = tblPatients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient to view vital history.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long patientId = (Long) patientsTableModel.getValueAt(selectedRow, 0);
        Patient patient = patientDao.findById(patientId);

        if (patient == null) {
            JOptionPane.showMessageDialog(this,
                    "Patient not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<PatientVitals> vitals = nurseService.getPatientVitalHistory(patientId);

            if (vitals.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No vital signs recorded for " + patient.getFullName(),
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            VitalHistoryDialog dialog = new VitalHistoryDialog(this, patient, vitals);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading vital history: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeTaskAction(ActionEvent e) {
        int selectedRow = tblTasks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to complete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long taskId = (Long) tasksTableModel.getValueAt(selectedRow, 0);
        String taskTitle = (String) tasksTableModel.getValueAt(selectedRow, 1);
        String status = (String) tasksTableModel.getValueAt(selectedRow, 4);

        if ("Completed".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "This task is already completed.",
                    "Already Completed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String completionNotes = JOptionPane.showInputDialog(this,
                "Enter completion notes for: " + taskTitle,
                "Complete Task",
                JOptionPane.QUESTION_MESSAGE);

        if (completionNotes != null) {
            try {
                nurseService.completeTask(taskId, completionNotes);
                loadTasksData();
                updatePatientStats();

                JOptionPane.showMessageDialog(this,
                        "Task completed successfully!",
                        "Task Completed", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error completing task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startTaskAction(ActionEvent e) {

        int selectedRow = tblTasks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to start.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long taskId = (Long) tasksTableModel.getValueAt(selectedRow, 0);
        String taskTitle = (String) tasksTableModel.getValueAt(selectedRow, 1);
        String status = (String) tasksTableModel.getValueAt(selectedRow, 4);

        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "This task is not in pending status.",
                    "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Start working on: " + taskTitle + "?",
                "Start Task",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                nurseService.startTask(taskId);
                loadTasksData();
                updatePatientStats();

                JOptionPane.showMessageDialog(this,
                        "Task started successfully!",
                        "Task Started", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error starting task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createTaskAction(ActionEvent e) {
        CreateTaskDialog dialog = new CreateTaskDialog(this, currentUser);
        dialog.setVisible(true);

        if (dialog.isCreated()) {
            loadTasksData();
            updatePatientStats();
        }
    }

    private void updateAppointmentStatusAction(ActionEvent e) {
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long appointmentId = (Long) appointmentsTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) appointmentsTableModel.getValueAt(selectedRow, 4);

        String[] statusOptions = {"Scheduled", "Completed", "Cancelled"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:",
                "Update Appointment Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                nurseService.updateAppointmentStatus(appointmentId, newStatus);
                loadAppointmentsData();

                JOptionPane.showMessageDialog(this,
                        "Appointment status updated successfully!",
                        "Status Updated", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error updating appointment status: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAppointmentDetailsAction(ActionEvent e) {
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment to view details.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long appointmentId = (Long) appointmentsTableModel.getValueAt(selectedRow, 0);
        Appointment appointment = appointmentDao.findById(appointmentId);

        if (appointment == null) {
            JOptionPane.showMessageDialog(this,
                    "Appointment not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String details = String.format(
                "Appointment Details:\n\n"
                + "ID: %d\n"
                + "Patient: %s\n"
                + "Doctor: %s\n"
                + "Date & Time: %s\n"
                + "Status: %s\n",
                appointment.getId(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                appointment.getStatus()
        );

        JOptionPane.showMessageDialog(this, details, "Appointment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateNursingReportAction(ActionEvent e) {

        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "User information not available.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String report = nurseService.generateNursingReport(currentUser.getId(), currentUser);
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating nursing report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateVitalsReportAction(ActionEvent e) {
        int selectedRow = tblPatients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient to generate vitals report.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long patientId = (Long) patientsTableModel.getValueAt(selectedRow, 0);

        try {
            String report = nurseService.generateVitalsReport(patientId);
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating vitals report: " + ex.getMessage(),
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
        fileChooser.setSelectedFile(new java.io.File("nursing_report.txt"));

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
