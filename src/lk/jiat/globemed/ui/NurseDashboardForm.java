package lk.jiat.globemed.ui;

import javax.swing.*;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.SecurityService;

public class NurseDashboardForm extends JFrame {

    private Staff currentUser;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JButton btnAppointments;
    private JTabbedPane tabMain;
    private JTable tblPatients;
    private JButton btnUpdateVitals;
    private JTable tblTasks;
    private JButton btnCompleteTask;

    private final SecurityService securityService = new SecurityService();

    public NurseDashboardForm() {
        initComponents();
        postInit(null);
    }

    public NurseDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void postInit(Staff user) {
        this.currentUser = user;
        setTitle("Nurse Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnUpdateVitals.addActionListener(e -> JOptionPane.showMessageDialog(this, "Update vitals (implement)."));
        btnCompleteTask.addActionListener(e -> JOptionPane.showMessageDialog(this, "Complete task (implement)."));

        boolean allowed = securityService.hasPermission(currentUser, "APPOINTMENT_MANAGE");
        btnAppointments.setEnabled(allowed);
        btnAppointments.addActionListener(e -> {
            if (!securityService.hasPermission(currentUser, "APPOINTMENT_MANAGE")) {
                JOptionPane.showMessageDialog(this, "You don't have permission to manage appointments.", "Access denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new AppointmentForm().setVisible(true);
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLayout(new java.awt.BorderLayout());

        JPanel header = new JPanel(new java.awt.BorderLayout());
        lblTitle = new JLabel("Nurse Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        btnLogout = new JButton("Logout");
        btnAppointments = new JButton("Appointments");
        JPanel right = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        right.add(btnAppointments);
        right.add(btnLogout);
        header.add(lblTitle, java.awt.BorderLayout.WEST);
        header.add(right, java.awt.BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(header, java.awt.BorderLayout.NORTH);

        tabMain = new JTabbedPane();

        JPanel tabPatientsPanel = new JPanel(new java.awt.BorderLayout());
        tblPatients = new JTable();
        tabPatientsPanel.add(new JScrollPane(tblPatients), java.awt.BorderLayout.CENTER);
        JPanel pv = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnUpdateVitals = new JButton("Update Vitals");
        pv.add(btnUpdateVitals);
        tabPatientsPanel.add(pv, java.awt.BorderLayout.SOUTH);

        JPanel tabTasksPanel = new JPanel(new java.awt.BorderLayout());
        tblTasks = new JTable();
        tabTasksPanel.add(new JScrollPane(tblTasks), java.awt.BorderLayout.CENTER);
        JPanel tv = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnCompleteTask = new JButton("Complete Task");
        tv.add(btnCompleteTask);
        tabTasksPanel.add(tv, java.awt.BorderLayout.SOUTH);

        tabMain.addTab("Patients", tabPatientsPanel);
        tabMain.addTab("Tasks", tabTasksPanel);

        add(tabMain, java.awt.BorderLayout.CENTER);
    }
}
