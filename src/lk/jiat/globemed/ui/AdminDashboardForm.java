package lk.jiat.globemed.ui;

import lk.jiat.globemed.dao.StaffDao;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.SecurityService;
import lk.jiat.globemed.service.SystemStatusService;
import lk.jiat.globemed.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class AdminDashboardForm extends JFrame {

    private Staff currentUser;

    // UI components
    private JPanel headerPanel;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnProfile;
    private JButton btnAppointments;
    private JSplitPane mainSplit;
    private JPanel navPanel;
    private JList<String> lstNav;
    private JTabbedPane tabMain;
    private JTable tblUsers;
    private JButton btnAddUser;
    private JButton btnEditUser;
    private JButton btnRemoveUser;
    private JButton btnGenerateReport;

    private final SecurityService securityService = new SecurityService();
    private final StaffDao staffDao = new StaffDao();
    private final SystemStatusService statusService = new SystemStatusService();
    private final ReportService reportService = new ReportService();
    
    // Additional UI components for enhanced functionality
    private DefaultTableModel userTableModel;
    private JTextArea txtSystemStatus;
    private JButton btnRefreshUsers;
    private JButton btnSystemStatus;
    private JButton btnPatientReport;
    private JButton btnStaffReport;
    private JButton btnFinancialReport;
    private JButton btnAppointmentReport;
    private JButton btnExportReport;
    private JTextField txtSystemName;
    private JTextField txtSystemVersion;
    private JCheckBox chkEmailNotifications;
    private JCheckBox chkSMSNotifications;
    private JCheckBox chkAuditLogging;
    private JButton btnSaveSettings;

    public AdminDashboardForm() {
        initComponents();
        postInit(null);
    }

    public AdminDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void postInit(Staff user) {
        this.currentUser = user;
        setTitle("Admin Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);
        initNavList();

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Profile: " + (currentUser != null ? currentUser.getName() : "Unknown"));
        });

        // Implement report functionality
        btnGenerateReport.addActionListener(this::generateReportAction);
        btnSystemStatus.addActionListener(this::showSystemStatusAction);
        btnPatientReport.addActionListener(e -> generateSpecificReport("PATIENT"));
        btnStaffReport.addActionListener(e -> generateSpecificReport("STAFF"));
        btnFinancialReport.addActionListener(e -> generateSpecificReport("FINANCIAL"));
        btnAppointmentReport.addActionListener(e -> generateSpecificReport("APPOINTMENT"));
        btnExportReport.addActionListener(this::exportReportAction);
        
        // Implement settings functionality
        btnSaveSettings.addActionListener(this::saveSettingsAction);

        // Implement user management functionality
        btnAddUser.addActionListener(this::addUserAction);
        btnEditUser.addActionListener(this::editUserAction);
        btnRemoveUser.addActionListener(this::removeUserAction);
        btnRefreshUsers.addActionListener(e -> loadUsersData());

        lstNav.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                String sel = lstNav.getSelectedValue();
                if ("User Management".equals(sel)) {
                    tabMain.setSelectedIndex(0);
                } else if ("Reports".equals(sel)) {
                    tabMain.setSelectedIndex(1);
                } else if ("System Settings".equals(sel)) {
                    tabMain.setSelectedIndex(2);
                }
            }
        });

        // Appointments button wiring
        boolean allowed = securityService.hasPermission(currentUser, "APPOINTMENT_MANAGE");
        btnAppointments.setEnabled(allowed);
        btnAppointments.addActionListener(e -> {
            if (!securityService.hasPermission(currentUser, "APPOINTMENT_MANAGE")) {
                JOptionPane.showMessageDialog(this, "You don't have permission to manage appointments.", "Access denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // open appointment UI
            AppointmentForm apf = new AppointmentForm();
            apf.setVisible(true);
        });
        
        // Load initial data
        loadUsersData();
        loadSystemSettings();
    }

    private void initNavList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("User Management");
        model.addElement("Reports");
        model.addElement("System Settings");
        lstNav.setModel(model);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Header Panel
        headerPanel = new JPanel(new BorderLayout(10, 10));
        lblTitle = new JLabel("🏥 Admin Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnProfile = new JButton("👤 Profile");
        btnAppointments = new JButton("📅 Appointments");
        btnLogout = new JButton("🚪 Logout");
        headerRight.add(btnProfile);
        headerRight.add(btnAppointments);
        headerRight.add(btnLogout);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(headerPanel, BorderLayout.NORTH);

        // Navigation Panel
        navPanel = new JPanel(new BorderLayout());
        lstNav = new JList<>();
        navPanel.add(new JScrollPane(lstNav), BorderLayout.CENTER);
        navPanel.setPreferredSize(new Dimension(200, 100));
        navPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        // Main Tabbed Pane
        tabMain = new JTabbedPane();

        // === USERS TAB ===
        JPanel tabUsers = createUsersTab();
        
        // === REPORTS TAB ===
        JPanel tabReports = createReportsTab();
        
        // === SETTINGS TAB ===
        JPanel tabSettings = createSettingsTab();

        tabMain.addTab("👥 Users", tabUsers);
        tabMain.addTab("📊 Reports", tabReports);
        tabMain.addTab("⚙️ Settings", tabSettings);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navPanel, tabMain);
        mainSplit.setDividerLocation(200);
        add(mainSplit, BorderLayout.CENTER);
    }
    
    private JPanel createUsersTab() {
        JPanel tabUsers = new JPanel(new BorderLayout(5, 5));
        tabUsers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Users table with model
        String[] columnNames = {"ID", "Name", "Email", "Role", "Status"};
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        tblUsers = new JTable(userTableModel);
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsers.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tblUsers);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Users"));
        tabUsers.add(scrollPane, BorderLayout.CENTER);
        
        // User management buttons
        JPanel userButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddUser = new JButton("➕ Add User");
        btnEditUser = new JButton("✏️ Edit User");
        btnRemoveUser = new JButton("🗑️ Remove User");
        btnRefreshUsers = new JButton("🔄 Refresh");
        
        userButtons.add(btnAddUser);
        userButtons.add(btnEditUser);
        userButtons.add(btnRemoveUser);
        userButtons.add(btnRefreshUsers);
        tabUsers.add(userButtons, BorderLayout.SOUTH);
        
        return tabUsers;
    }
    
    private JPanel createReportsTab() {
        JPanel tabReports = new JPanel(new BorderLayout(5, 5));
        tabReports.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Report generation panel
        JPanel reportPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // System Status Section
        JLabel lblSystemStatus = new JLabel("📊 System Status:");
        lblSystemStatus.setFont(lblSystemStatus.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        reportPanel.add(lblSystemStatus, gbc);
        
        btnSystemStatus = new JButton("📈 View System Status");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        reportPanel.add(btnSystemStatus, gbc);
        
        // Reports Section
        JLabel lblReports = new JLabel("📋 Generate Reports:");
        lblReports.setFont(lblReports.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        reportPanel.add(lblReports, gbc);
        
        btnPatientReport = new JButton("👥 Patient Report");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        reportPanel.add(btnPatientReport, gbc);
        
        btnStaffReport = new JButton("👨‍⚕️ Staff Report");
        gbc.gridx = 1; gbc.gridy = 3;
        reportPanel.add(btnStaffReport, gbc);
        
        btnFinancialReport = new JButton("💰 Financial Report");
        gbc.gridx = 0; gbc.gridy = 4;
        reportPanel.add(btnFinancialReport, gbc);
        
        btnAppointmentReport = new JButton("📅 Appointment Report");
        gbc.gridx = 1; gbc.gridy = 4;
        reportPanel.add(btnAppointmentReport, gbc);
        
        btnGenerateReport = new JButton("📋 Generate Custom Report");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        reportPanel.add(btnGenerateReport, gbc);
        
        btnExportReport = new JButton("📤 Export Last Report");
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1;
        reportPanel.add(btnExportReport, gbc);
        
        tabReports.add(reportPanel, BorderLayout.NORTH);
        
        // System status display area
        txtSystemStatus = new JTextArea(15, 40);
        txtSystemStatus.setEditable(false);
        txtSystemStatus.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane statusScrollPane = new JScrollPane(txtSystemStatus);
        statusScrollPane.setBorder(BorderFactory.createTitledBorder("System Information"));
        tabReports.add(statusScrollPane, BorderLayout.CENTER);
        
        return tabReports;
    }
    
    private JPanel createSettingsTab() {
        JPanel tabSettings = new JPanel(new BorderLayout(5, 5));
        tabSettings.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // System Information Section
        JLabel lblSystemInfo = new JLabel("🏥 System Information:");
        lblSystemInfo.setFont(lblSystemInfo.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        settingsPanel.add(lblSystemInfo, gbc);
        
        JLabel lblSystemName = new JLabel("System Name:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        settingsPanel.add(lblSystemName, gbc);
        
        txtSystemName = new JTextField("GlobeMed Healthcare Management System", 25);
        gbc.gridx = 1; gbc.gridy = 1;
        settingsPanel.add(txtSystemName, gbc);
        
        JLabel lblSystemVersion = new JLabel("Version:");
        gbc.gridx = 0; gbc.gridy = 2;
        settingsPanel.add(lblSystemVersion, gbc);
        
        txtSystemVersion = new JTextField("1.0.0", 25);
        gbc.gridx = 1; gbc.gridy = 2;
        settingsPanel.add(txtSystemVersion, gbc);
        
        // Notification Settings Section
        JLabel lblNotifications = new JLabel("📧 Notification Settings:");
        lblNotifications.setFont(lblNotifications.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        settingsPanel.add(lblNotifications, gbc);
        
        chkEmailNotifications = new JCheckBox("Enable Email Notifications", true);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        settingsPanel.add(chkEmailNotifications, gbc);
        
        chkSMSNotifications = new JCheckBox("Enable SMS Notifications", true);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        settingsPanel.add(chkSMSNotifications, gbc);
        
        // Security Settings Section
        JLabel lblSecurity = new JLabel("🔒 Security Settings:");
        lblSecurity.setFont(lblSecurity.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        settingsPanel.add(lblSecurity, gbc);
        
        chkAuditLogging = new JCheckBox("Enable Audit Logging", true);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        settingsPanel.add(chkAuditLogging, gbc);
        
        // Save button
        btnSaveSettings = new JButton("💾 Save Settings");
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        settingsPanel.add(btnSaveSettings, gbc);
        
        tabSettings.add(settingsPanel, BorderLayout.NORTH);
        
        return tabSettings;
    }
    
    // ===== USER MANAGEMENT ACTIONS =====
    
    private void loadUsersData() {
        try {
            List<Staff> allStaff = staffDao.findAll();
            userTableModel.setRowCount(0); // Clear existing data
            
            for (Staff staff : allStaff) {
                Object[] row = {
                    staff.getId(),
                    staff.getName(),
                    staff.getEmail(),
                    staff.getRole() != null ? staff.getRole().getName() : "No Role",
                    "Active" // You can add a status field to Staff model if needed
                };
                userTableModel.addRow(row);
            }
            
            System.out.println("✅ Loaded " + allStaff.size() + " users into table");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading users: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void addUserAction(ActionEvent e) {
        AddUserDialog dialog = new AddUserDialog(this);
        dialog.setVisible(true);
        
        Staff newStaff = dialog.getCreatedStaff();
        if (newStaff != null) {
            try {
                staffDao.create(newStaff);
                loadUsersData(); // Refresh the table
                JOptionPane.showMessageDialog(this, 
                    "User '" + newStaff.getName() + "' created successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error creating user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void editUserAction(ActionEvent e) {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long userId = (Long) userTableModel.getValueAt(selectedRow, 0);
        Staff staffToEdit = staffDao.findById(userId);
        
        if (staffToEdit == null) {
            JOptionPane.showMessageDialog(this, 
                "User not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        EditUserDialog dialog = new EditUserDialog(this, staffToEdit);
        dialog.setVisible(true);
        
        Staff editedStaff = dialog.getEditedStaff();
        if (editedStaff != null) {
            try {
                staffDao.update(editedStaff);
                loadUsersData(); // Refresh the table
                JOptionPane.showMessageDialog(this, 
                    "User '" + editedStaff.getName() + "' updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void removeUserAction(ActionEvent e) {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user to remove.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long userId = (Long) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        String userEmail = (String) userTableModel.getValueAt(selectedRow, 2);
        
        // Prevent deletion of current user
        if (currentUser != null && currentUser.getId().equals(userId)) {
            JOptionPane.showMessageDialog(this, 
                "You cannot delete your own account!", 
                "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user '" + userName + "' (" + userEmail + ")?\n" +
            "This action cannot be undone!", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = staffDao.deleteById(userId);
                if (deleted) {
                    loadUsersData(); // Refresh the table
                    JOptionPane.showMessageDialog(this, 
                        "User '" + userName + "' deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete user. User may not exist.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    // ===== REPORT ACTIONS =====
    
    private void generateReportAction(ActionEvent e) {
        String[] reportTypes = {"Patient Report", "Staff Report", "Financial Report", "Appointment Report", "System Status"};
        String selectedType = (String) JOptionPane.showInputDialog(this, 
            "Select report type:", 
            "Generate Report", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            reportTypes, 
            reportTypes[0]);
        
        if (selectedType != null) {
            generateSpecificReport(selectedType.split(" ")[0].toUpperCase());
        }
    }
    
    private void generateSpecificReport(String reportType) {
        try {
            String reportContent = "";
            
            switch (reportType) {
                case "PATIENT":
                    reportContent = reportService.generatePatientReport();
                    break;
                case "STAFF":
                    reportContent = reportService.generateStaffReport();
                    break;
                case "FINANCIAL":
                    reportContent = reportService.generateFinancialReport();
                    break;
                case "APPOINTMENT":
                    reportContent = reportService.generateAppointmentReport();
                    break;
                default:
                    reportContent = "Report type not implemented yet.";
            }
            
            // Display report in a new dialog
            JDialog reportDialog = new JDialog(this, reportType + " Report", true);
            reportDialog.setSize(600, 500);
            reportDialog.setLocationRelativeTo(this);
            
            JTextArea reportArea = new JTextArea(reportContent);
            reportArea.setEditable(false);
            reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(reportArea);
            reportDialog.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(ev -> reportDialog.dispose());
            buttonPanel.add(closeButton);
            reportDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            reportDialog.setVisible(true);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showSystemStatusAction(ActionEvent e) {
        try {
            String statusInfo = statusService.getSystemHealthSummary();
            Map<String, Object> stats = statusService.getSystemStatistics();
            
            StringBuilder detailedStatus = new StringBuilder();
            detailedStatus.append("=== GLOBEMED SYSTEM STATUS ===\n\n");
            detailedStatus.append("📊 SYSTEM HEALTH:\n");
            detailedStatus.append("Database Connected: ").append(stats.get("databaseConnected")).append("\n");
            detailedStatus.append("System Initialized: ").append(stats.get("systemInitialized")).append("\n\n");
            
            detailedStatus.append("👥 USER STATISTICS:\n");
            detailedStatus.append("Total Patients: ").append(stats.get("totalPatients")).append("\n");
            detailedStatus.append("Total Staff: ").append(stats.get("totalStaff")).append("\n");
            detailedStatus.append("  - Doctors: ").append(stats.get("totalDoctors")).append("\n");
            detailedStatus.append("  - Nurses: ").append(stats.get("totalNurses")).append("\n");
            detailedStatus.append("  - Pharmacists: ").append(stats.get("totalPharmacists")).append("\n");
            detailedStatus.append("  - Accountants: ").append(stats.get("totalAccountants")).append("\n");
            detailedStatus.append("  - Administrators: ").append(stats.get("totalAdmins")).append("\n\n");
            
            detailedStatus.append("📅 APPOINTMENT STATISTICS:\n");
            detailedStatus.append("Total Appointments: ").append(stats.get("totalAppointments")).append("\n");
            detailedStatus.append("Scheduled: ").append(stats.get("scheduledAppointments")).append("\n");
            detailedStatus.append("Completed: ").append(stats.get("completedAppointments")).append("\n");
            detailedStatus.append("Cancelled: ").append(stats.get("cancelledAppointments")).append("\n\n");
            
            detailedStatus.append("💰 FINANCIAL STATISTICS:\n");
            detailedStatus.append("Total Billing Records: ").append(stats.get("totalBillingRecords")).append("\n");
            detailedStatus.append("Paid Bills: ").append(stats.get("paidBills")).append("\n");
            detailedStatus.append("Pending Bills: ").append(stats.get("pendingBills")).append("\n");
            detailedStatus.append("Total Revenue: $").append(String.format("%.2f", stats.get("totalRevenue"))).append("\n\n");
            
            detailedStatus.append("🕐 Last Updated: ").append(new java.util.Date()).append("\n");
            detailedStatus.append("===============================");
            
            txtSystemStatus.setText(detailedStatus.toString());
            tabMain.setSelectedIndex(1); // Switch to Reports tab
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error retrieving system status: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void exportReportAction(ActionEvent e) {
        String content = txtSystemStatus.getText();
        if (content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No report data to export. Please generate a report first.", 
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("GlobeMed_Report_" + 
            new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write(content);
                writer.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Report exported successfully to:\n" + fileChooser.getSelectedFile().getAbsolutePath(), 
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting report: " + ex.getMessage(), 
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    // ===== SETTINGS ACTIONS =====
    
    private void loadSystemSettings() {
        // Load current system settings (in a real application, these would come from a database or config file)
        txtSystemName.setText("GlobeMed Healthcare Management System");
        txtSystemVersion.setText("1.0.0");
        chkEmailNotifications.setSelected(true);
        chkSMSNotifications.setSelected(true);
        chkAuditLogging.setSelected(true);
    }
    
    private void saveSettingsAction(ActionEvent e) {
        try {
            // In a real application, you would save these settings to a database or configuration file
            String systemName = txtSystemName.getText().trim();
            String systemVersion = txtSystemVersion.getText().trim();
            boolean emailEnabled = chkEmailNotifications.isSelected();
            boolean smsEnabled = chkSMSNotifications.isSelected();
            boolean auditEnabled = chkAuditLogging.isSelected();
            
            if (systemName.isEmpty() || systemVersion.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "System name and version cannot be empty!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Simulate saving settings
            StringBuilder settingsInfo = new StringBuilder();
            settingsInfo.append("Settings saved successfully!\n\n");
            settingsInfo.append("System Name: ").append(systemName).append("\n");
            settingsInfo.append("Version: ").append(systemVersion).append("\n");
            settingsInfo.append("Email Notifications: ").append(emailEnabled ? "Enabled" : "Disabled").append("\n");
            settingsInfo.append("SMS Notifications: ").append(smsEnabled ? "Enabled" : "Disabled").append("\n");
            settingsInfo.append("Audit Logging: ").append(auditEnabled ? "Enabled" : "Disabled").append("\n");
            
            JOptionPane.showMessageDialog(this, 
                settingsInfo.toString(), 
                "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
                
            System.out.println("✅ System settings updated by " + 
                (currentUser != null ? currentUser.getName() : "Unknown User"));
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error saving settings: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
