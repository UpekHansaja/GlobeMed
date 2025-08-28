package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.SecurityService;

import javax.swing.*;
import java.awt.*;

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

        btnGenerateReport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Generating admin report... (implement ReportService)");
        });

        btnAddUser.addActionListener(e -> JOptionPane.showMessageDialog(this, "Open Add User dialog (implement)."));
        btnEditUser.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit selected user (implement)."));
        btnRemoveUser.addActionListener(e -> JOptionPane.showMessageDialog(this, "Remove selected user (implement)."));

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
        btnAppointments = new JButton("Appointments");
        btnLogout = new JButton("Logout");
        headerRight.add(btnProfile);
        headerRight.add(btnAppointments);
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
