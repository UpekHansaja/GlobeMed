package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lk.jiat.globemed.dao.BillingDao;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.model.BillingRecord;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.AccountantService;
import lk.jiat.globemed.service.BillingService;

public class AccountantDashboardForm extends JFrame {

    private Staff currentUser;
    private final AccountantService accountantService = new AccountantService();
    private final BillingService billingService = new BillingService();
    private final BillingDao billingDao = new BillingDao();
    private final PatientDao patientDao = new PatientDao();

    // UI Components
    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnProfile;
    private JTabbedPane tabMain;

    // Billing Tab Components
    private JTable tblBilling;
    private DefaultTableModel billingTableModel;
    private JButton btnProcessPayment;
    private JButton btnRejectPayment;
    private JButton btnCreateBilling;
    private JButton btnRefreshBilling;
    private JComboBox<String> cmbStatusFilter;
    private JLabel lblFinancialStats;

    // Payments Tab Components
    private JTable tblPayments;
    private DefaultTableModel paymentsTableModel;
    private JButton btnViewPaymentDetails;
    private JButton btnRefreshPayments;
    private JComboBox<String> cmbPaymentMethodFilter;

    // Reports Tab Components
    private JTextArea txtReportArea;
    private JButton btnGenerateFinancialReport;
    private JButton btnGenerateRevenueReport;
    private JButton btnGenerateOutstandingReport;
    private JButton btnExportReport;

    public AccountantDashboardForm() {
        initComponents();
        postInit(null);
    }

    public AccountantDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        lblTitle = new JLabel("💰 Accountant Dashboard");
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
        tabMain.addTab("💳 Billing", createBillingTab());
        tabMain.addTab("💰 Payments", createPaymentsTab());
        tabMain.addTab("📊 Reports", createReportsTab());

        add(tabMain, BorderLayout.CENTER);
    }

    private JPanel createBillingTab() {
        JPanel tabBilling = new JPanel(new BorderLayout(5, 5));
        tabBilling.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with filters and stats
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Status Filter:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Pending", "Paid", "Rejected"});
        filterPanel.add(cmbStatusFilter);

        topPanel.add(filterPanel, BorderLayout.WEST);

        // Stats panel
        lblFinancialStats = new JLabel("Loading financial statistics...");
        lblFinancialStats.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(lblFinancialStats, BorderLayout.EAST);

        tabBilling.add(topPanel, BorderLayout.NORTH);

        // Billing table
        String[] columnNames = {"ID", "Patient", "Amount", "Payment Method", "Status", "Created Date"};
        billingTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBilling = new JTable(billingTableModel);
        tblBilling.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBilling.getTableHeader().setReorderingAllowed(false);

        // Custom cell renderer for status highlighting
        tblBilling.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 4);
                    if ("Pending".equals(status)) {
                        c.setBackground(new Color(128, 97, 6)); // yellow
                    } else if ("Paid".equals(status)) {
                        c.setBackground(new Color(4, 89, 46)); // Light green
                    } else if ("Rejected".equals(status)) {
                        c.setBackground(new Color(99, 3, 3)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBilling);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Billing Records"));
        tabBilling.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnProcessPayment = new JButton("✅ Process Payment");
        btnRejectPayment = new JButton("❌ Reject Payment");
        btnCreateBilling = new JButton("➕ Create Billing");
        btnRefreshBilling = new JButton("🔄 Refresh");

        buttonPanel.add(btnProcessPayment);
        buttonPanel.add(btnRejectPayment);
        buttonPanel.add(btnCreateBilling);
        buttonPanel.add(btnRefreshBilling);

        tabBilling.add(buttonPanel, BorderLayout.SOUTH);

        return tabBilling;
    }

    private JPanel createPaymentsTab() {
        JPanel tabPayments = new JPanel(new BorderLayout(5, 5));
        tabPayments.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Payment Method:"));
        cmbPaymentMethodFilter = new JComboBox<>(new String[]{"All", "Cash", "Card", "Insurance", "Bank Transfer"});
        filterPanel.add(cmbPaymentMethodFilter);

        tabPayments.add(filterPanel, BorderLayout.NORTH);

        // Payments table
        String[] columnNames = {"ID", "Patient", "Amount", "Payment Method", "Status", "Date", "Time"};
        paymentsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPayments = new JTable(paymentsTableModel);
        tblPayments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPayments.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tblPayments);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payment History"));
        tabPayments.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnViewPaymentDetails = new JButton("👁️ View Details");
        btnRefreshPayments = new JButton("🔄 Refresh");

        buttonPanel.add(btnViewPaymentDetails);
        buttonPanel.add(btnRefreshPayments);

        tabPayments.add(buttonPanel, BorderLayout.SOUTH);

        return tabPayments;
    }

    private JPanel createReportsTab() {
        JPanel tabReports = new JPanel(new BorderLayout(5, 5));
        tabReports.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerateFinancialReport = new JButton("📊 Financial Report");
        btnGenerateRevenueReport = new JButton("💰 Revenue Report");
        btnGenerateOutstandingReport = new JButton("⚠️ Outstanding Payments");
        btnExportReport = new JButton("📤 Export Report");

        buttonPanel.add(btnGenerateFinancialReport);
        buttonPanel.add(btnGenerateRevenueReport);
        buttonPanel.add(btnGenerateOutstandingReport);
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
        setTitle("💰 Accountant Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);

        // Event handlers
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Accountant: " + (currentUser != null ? currentUser.getName() : "Unknown") + "\n"
                    + "Email: " + (currentUser != null ? currentUser.getEmail() : "Unknown"),
                    "Profile Information", JOptionPane.INFORMATION_MESSAGE);
        });

        // Billing actions
        btnProcessPayment.addActionListener(this::processPaymentAction);
        btnRejectPayment.addActionListener(this::rejectPaymentAction);
        btnCreateBilling.addActionListener(this::createBillingAction);
        btnRefreshBilling.addActionListener(e -> loadBillingData());

        // Filter actions
        cmbStatusFilter.addActionListener(e -> filterBillingByStatus());
        cmbPaymentMethodFilter.addActionListener(e -> filterPaymentsByMethod());

        // Payment actions
        btnViewPaymentDetails.addActionListener(this::viewPaymentDetailsAction);
        btnRefreshPayments.addActionListener(e -> loadPaymentsData());

        // Report actions
        btnGenerateFinancialReport.addActionListener(this::generateFinancialReportAction);
        btnGenerateRevenueReport.addActionListener(this::generateRevenueReportAction);
        btnGenerateOutstandingReport.addActionListener(this::generateOutstandingReportAction);
        btnExportReport.addActionListener(this::exportReportAction);

        // Load initial data
        loadBillingData();
        loadPaymentsData();
        updateFinancialStats();
    }

    // ===== DATA LOADING METHODS =====
    private void loadBillingData() {
        try {
            List<BillingRecord> billingRecords = billingDao.findAll();
            billingTableModel.setRowCount(0);

            for (BillingRecord record : billingRecords) {
                Object[] row = {
                    record.getId(),
                    record.getPatient().getFullName(),
                    String.format("$%.2f", record.getAmount()),
                    record.getPaymentMethod(),
                    record.getStatus(),
                    record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                };
                billingTableModel.addRow(row);
            }

            updateFinancialStats();
            System.out.println("✅ Loaded " + billingRecords.size() + " billing records");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading billing data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadPaymentsData() {
        try {
            List<BillingRecord> paidRecords = accountantService.getPaidRecords();
            paymentsTableModel.setRowCount(0);

            for (BillingRecord record : paidRecords) {
                Object[] row = {
                    record.getId(),
                    record.getPatient().getFullName(),
                    String.format("$%.2f", record.getAmount()),
                    record.getPaymentMethod(),
                    record.getStatus(),
                    record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    record.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"))
                };
                paymentsTableModel.addRow(row);
            }

            System.out.println("✅ Loaded " + paidRecords.size() + " payment records");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading payment data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateFinancialStats() {
        try {
            Map<String, Object> stats = accountantService.getFinancialStatistics();
            String statsText = String.format(
                    "💰 Revenue: $%.2f | 📋 Total: %s | ✅ Paid: %s | ⏳ Pending: %s | ❌ Rejected: %s",
                    stats.get("totalRevenue"),
                    stats.get("totalRecords"),
                    stats.get("paidRecords"),
                    stats.get("pendingRecords"),
                    stats.get("rejectedRecords")
            );
            lblFinancialStats.setText(statsText);
        } catch (Exception e) {
            lblFinancialStats.setText("Error loading statistics");
        }
    }

    // ===== FILTER METHODS =====
    private void filterBillingByStatus() {
        String selectedStatus = (String) cmbStatusFilter.getSelectedItem();
        if ("All".equals(selectedStatus)) {
            loadBillingData();
            return;
        }

        try {
            List<BillingRecord> filteredRecords = accountantService.getBillingRecordsByStatus(selectedStatus);
            billingTableModel.setRowCount(0);

            for (BillingRecord record : filteredRecords) {
                Object[] row = {
                    record.getId(),
                    record.getPatient().getFullName(),
                    String.format("$%.2f", record.getAmount()),
                    record.getPaymentMethod(),
                    record.getStatus(),
                    record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                };
                billingTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering billing records: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterPaymentsByMethod() {
        String selectedMethod = (String) cmbPaymentMethodFilter.getSelectedItem();
        if ("All".equals(selectedMethod)) {
            loadPaymentsData();
            return;
        }

        try {
            List<BillingRecord> allPaidRecords = accountantService.getPaidRecords();
            paymentsTableModel.setRowCount(0);

            for (BillingRecord record : allPaidRecords) {
                if (record.getPaymentMethod().equals(selectedMethod)) {
                    Object[] row = {
                        record.getId(),
                        record.getPatient().getFullName(),
                        String.format("$%.2f", record.getAmount()),
                        record.getPaymentMethod(),
                        record.getStatus(),
                        record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        record.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"))
                    };
                    paymentsTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering payments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== ACTION METHODS =====
    private void processPaymentAction(ActionEvent e) {
        int selectedRow = tblBilling.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a billing record to process payment.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long billingId = (Long) billingTableModel.getValueAt(selectedRow, 0);
        BillingRecord billing = billingDao.findById(billingId);

        if (billing == null) {
            JOptionPane.showMessageDialog(this,
                    "Billing record not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!"Pending".equals(billing.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "This payment has already been processed or rejected.",
                    "Already Processed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Payment method selection dialog
        String[] paymentMethods = {"Cash", "Card", "Insurance", "Bank Transfer"};
        String selectedMethod = (String) JOptionPane.showInputDialog(this,
                "Select payment method for $" + String.format("%.2f", billing.getAmount()) + ":",
                "Process Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                paymentMethods,
                paymentMethods[0]);

        if (selectedMethod != null) {
            try {
                accountantService.processPayment(billingId, selectedMethod);
                loadBillingData();
                loadPaymentsData();
                updateFinancialStats();

                JOptionPane.showMessageDialog(this,
                        "Payment processed successfully!\nAmount: $" + String.format("%.2f", billing.getAmount())
                        + "\nMethod: " + selectedMethod,
                        "Payment Processed", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error processing payment: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectPaymentAction(ActionEvent e) {
        int selectedRow = tblBilling.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a billing record to reject.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long billingId = (Long) billingTableModel.getValueAt(selectedRow, 0);
        BillingRecord billing = billingDao.findById(billingId);

        if (billing == null) {
            JOptionPane.showMessageDialog(this,
                    "Billing record not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!"Pending".equals(billing.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "This payment has already been processed or rejected.",
                    "Already Processed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this,
                "Enter reason for rejection:",
                "Reject Payment",
                JOptionPane.QUESTION_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            try {
                accountantService.rejectPayment(billingId, reason);
                loadBillingData();
                updateFinancialStats();

                JOptionPane.showMessageDialog(this,
                        "Payment rejected successfully!\nReason: " + reason,
                        "Payment Rejected", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error rejecting payment: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createBillingAction(ActionEvent e) {
        CreateBillingDialog dialog = new CreateBillingDialog(this);
        dialog.setVisible(true);

        if (dialog.isCreated()) {
            loadBillingData();
            updateFinancialStats();
        }
    }

    private void viewPaymentDetailsAction(ActionEvent e) {
        int selectedRow = tblPayments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a payment record to view details.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long billingId = (Long) paymentsTableModel.getValueAt(selectedRow, 0);
        BillingRecord billing = billingDao.findById(billingId);

        if (billing == null) {
            JOptionPane.showMessageDialog(this,
                    "Payment record not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String details = String.format(
                "Payment Details:\n\n"
                + "ID: %d\n"
                + "Patient: %s\n"
                + "Amount: $%.2f\n"
                + "Payment Method: %s\n"
                + "Status: %s\n"
                + "Created Date: %s\n",
                billing.getId(),
                billing.getPatient().getFullName(),
                billing.getAmount(),
                billing.getPaymentMethod(),
                billing.getStatus(),
                billing.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        JOptionPane.showMessageDialog(this, details, "Payment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== REPORT GENERATION METHODS =====
    private void generateFinancialReportAction(ActionEvent e) {
        try {
            String report = accountantService.generateFinancialReport();
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating financial report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateRevenueReportAction(ActionEvent e) {
        // Simple date range selection for now
        String startDateStr = JOptionPane.showInputDialog(this,
                "Enter start date (yyyy-MM-dd):",
                "Revenue Report",
                JOptionPane.QUESTION_MESSAGE);

        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            return;
        }

        String endDateStr = JOptionPane.showInputDialog(this,
                "Enter end date (yyyy-MM-dd):",
                "Revenue Report",
                JOptionPane.QUESTION_MESSAGE);

        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            return;
        }

        try {
            java.time.LocalDateTime startDate = java.time.LocalDate.parse(startDateStr).atStartOfDay();
            java.time.LocalDateTime endDate = java.time.LocalDate.parse(endDateStr).atTime(23, 59, 59);

            String report = accountantService.generateRevenueReport(startDate, endDate);
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating revenue report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateOutstandingReportAction(ActionEvent e) {
        try {
            String report = accountantService.generateOutstandingPaymentsReport();
            txtReportArea.setText(report);
            txtReportArea.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating outstanding payments report: " + ex.getMessage(),
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
        fileChooser.setSelectedFile(new java.io.File("financial_report.txt"));

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
