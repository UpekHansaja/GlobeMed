package lk.jiat.globemed.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lk.jiat.globemed.dao.RoleDao;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.report.CSVReportVisitor;
import lk.jiat.globemed.report.ReportVisitor;
import lk.jiat.globemed.service.StaffService;
import lk.jiat.globemed.service.command.*;

public class AdminDashboardForm extends JFrame {

    private final StaffService staffService = new StaffService();
    private final CommandInvoker invoker = new CommandInvoker();

    private Staff currentUser;

    // UI components
    private JPanel headerPanel;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnProfile;
    private JSplitPane mainSplit;
    private JPanel navPanel;
    private JList<String> lstNav;
    private JTabbedPane tabMain;
    private JTable tblUsers;
    private JButton btnAddUser;
    private JButton btnEditUser;
    private JButton btnRemoveUser;
    private JButton btnGenerateReport;

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

        // populate table
        refreshUsersTable();

        btnAddUser.addActionListener(e -> onAddUser());
        btnEditUser.addActionListener(e -> onEditUser());
        btnRemoveUser.addActionListener(e -> onDeleteUser());
        btnGenerateReport.addActionListener(e -> onExportUsersCSV());
    }

    private void refreshUsersTable() {
        List<Staff> users = staffService.findAll();
        String[] cols = {"ID", "Name", "Email", "Role"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (Staff s : users) {
            String role = s.getRole() != null ? s.getRole().getName() : "";
            Object[] row = new Object[]{s.getId(), s.getName(), s.getEmail(), role};
            m.addRow(row);
        }
        tblUsers.setModel(m);
    }

    private void onAddUser() {
        // Simple dialog using JOptionPane inputs (replace with proper Form later)
        String name = JOptionPane.showInputDialog(this, "Name:");
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        String email = JOptionPane.showInputDialog(this, "Email:");
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        String pwd = JOptionPane.showInputDialog(this, "Password:");
        if (pwd == null) {
            return;
        }
        String roleName = JOptionPane.showInputDialog(this, "Role (Admin/Doctor/Nurse/Pharmacist/Accountant):", "Admin");
        if (roleName == null) {
            return;
        }

        Staff s = new Staff();
        s.setName(name.trim());
        s.setEmail(email.trim());
        s.setPassword(pwd);
        // set role if exists
        RoleDao rdao = new RoleDao();
        var role = rdao.findByName(roleName.trim());
        s.setRole(role);

        AddUserCommand cmd = new AddUserCommand(staffService, s, currentUser != null ? currentUser.getEmail() : "system");
        invoker.executeCommand(cmd);
        refreshUsersTable();
        JOptionPane.showMessageDialog(this, "User created.");
    }

    private void onEditUser() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }
        Long id = ((Number) tblUsers.getValueAt(row, 0)).longValue();
        Staff existing = staffService.findByEmail((String) tblUsers.getValueAt(row, 2));
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "User not found.");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Name:", existing.getName());
        if (name == null) {
            return;
        }
        String pwd = JOptionPane.showInputDialog(this, "Password:", existing.getPassword());
        if (pwd == null) {
            return;
        }
        String roleName = JOptionPane.showInputDialog(this, "Role:", existing.getRole() != null ? existing.getRole().getName() : "Admin");
        if (roleName == null) {
            return;
        }

        existing.setName(name.trim());
        existing.setPassword(pwd);
        RoleDao rdao = new RoleDao();
        existing.setRole(rdao.findByName(roleName.trim()));

        EditUserCommand cmd = new EditUserCommand(staffService, existing, currentUser != null ? currentUser.getEmail() : "system");
        invoker.executeCommand(cmd);
        refreshUsersTable();
        JOptionPane.showMessageDialog(this, "User updated.");
    }

    private void onDeleteUser() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }
        Long id = ((Number) tblUsers.getValueAt(row, 0)).longValue();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete user id=" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        DeleteUserCommand cmd = new DeleteUserCommand(staffService, id, currentUser != null ? currentUser.getEmail() : "system");
        invoker.executeCommand(cmd);
        refreshUsersTable();
        JOptionPane.showMessageDialog(this, "User deleted.");
    }

    private void onExportUsersCSV() {
        List<Staff> users = staffService.findAll();
        String path = System.getProperty("user.home") + "/globemed_staff_report.csv";
        ReportVisitor visitor = new CSVReportVisitor(path);
        visitor.visitStaffList(users);
        JOptionPane.showMessageDialog(this, "CSV exported to: " + path);
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
        setSize(900, 650);
        setLayout(new BorderLayout());

        // header
        headerPanel = new JPanel(new BorderLayout(10, 10));
        lblTitle = new JLabel("Admin Dashboard");
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

        // main split
        navPanel = new JPanel(new BorderLayout());
        lstNav = new JList<>();
        navPanel.add(new JScrollPane(lstNav), BorderLayout.CENTER);
        navPanel.setPreferredSize(new Dimension(200, 100));

        tabMain = new JTabbedPane();

        // Tab Users
        JPanel tabUsers = new JPanel(new BorderLayout());
        tblUsers = new JTable();
        tabUsers.add(new JScrollPane(tblUsers), BorderLayout.CENTER);
        JPanel userButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddUser = new JButton("Add User");
        btnEditUser = new JButton("Edit User");
        btnRemoveUser = new JButton("Remove User");
        userButtons.add(btnAddUser);
        userButtons.add(btnEditUser);
        userButtons.add(btnRemoveUser);
        tabUsers.add(userButtons, BorderLayout.SOUTH);

        // Tab Reports
        JPanel tabReports = new JPanel(new BorderLayout());
        btnGenerateReport = new JButton("Generate Report");
        tabReports.add(btnGenerateReport, BorderLayout.NORTH);

        // Tab Settings
        JPanel tabSettings = new JPanel(new BorderLayout());
        tabSettings.add(new JLabel("System settings"), BorderLayout.CENTER);

        tabMain.addTab("Users", tabUsers);
        tabMain.addTab("Reports", tabReports);
        tabMain.addTab("Settings", tabSettings);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navPanel, tabMain);
        mainSplit.setDividerLocation(200);
        add(mainSplit, BorderLayout.CENTER);
    }
}
