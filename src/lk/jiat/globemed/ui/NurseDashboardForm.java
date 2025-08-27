package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Staff;

import javax.swing.*;
import java.awt.*;

public class NurseDashboardForm extends JFrame {

    private Staff currentUser;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JTabbedPane tabMain;
    private JTable tblPatients;
    private JButton btnUpdateVitals;
    private JTable tblTasks;
    private JButton btnCompleteTask;

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
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        lblTitle = new JLabel("Nurse Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        btnLogout = new JButton("Logout");
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(header, BorderLayout.NORTH);

        tabMain = new JTabbedPane();

        JPanel tabPatientsPanel = new JPanel(new BorderLayout());
        tblPatients = new JTable();
        tabPatientsPanel.add(new JScrollPane(tblPatients), BorderLayout.CENTER);
        JPanel pv = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUpdateVitals = new JButton("Update Vitals");
        pv.add(btnUpdateVitals);
        tabPatientsPanel.add(pv, BorderLayout.SOUTH);

        JPanel tabTasksPanel = new JPanel(new BorderLayout());
        tblTasks = new JTable();
        tabTasksPanel.add(new JScrollPane(tblTasks), BorderLayout.CENTER);
        JPanel tv = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCompleteTask = new JButton("Complete Task");
        tv.add(btnCompleteTask);
        tabTasksPanel.add(tv, BorderLayout.SOUTH);

        tabMain.addTab("Patients", tabPatientsPanel);
        tabMain.addTab("Tasks", tabTasksPanel);

        add(tabMain, BorderLayout.CENTER);
    }
}